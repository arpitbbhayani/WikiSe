package com.wikise.util;

import com.wikise.parse.WikiPageConcise;

import java.io.*;
import java.util.PriorityQueue;

/**
 * Created by Arpit Bhayani on 29/1/14.
 */
public class FileIO {

    int WORD_BUFFER_SIZE = 500;

    private String temporaryFilePrefix = "tempfile";
    private String dictionaryFileName = "meta/dictionary.dat";
    private String metaFileName = "meta/metadata.dat";
    private String infoFileName = "meta/info.dat";

    private String indexFolderPath = null;

    private int availableFiles = 0;

    private BufferedWriter metaWriter = null , temporaryFileWriter = null , infoWriter = null, dictionaryWriter = null;
    long infoFileSeekLocation = 0;

    /**
     * Public constructor.
     * @param indexFolderPath is the path of the folder where in indexes are to be saved.
     */
    public FileIO(String indexFolderPath) {
        if ( indexFolderPath.charAt(indexFolderPath.length()-1) != '/' ) {
            this.indexFolderPath = indexFolderPath + '/';
        }
        else {
            this.indexFolderPath = indexFolderPath;
        }
    }

    /**
     * Initializes the fileIO streams.
     */
    public void initialize() {
        try {
            dictionaryWriter = new BufferedWriter(new FileWriter(indexFolderPath + dictionaryFileName));
            infoWriter = new BufferedWriter(new FileWriter(indexFolderPath + infoFileName));
            metaWriter = new BufferedWriter(new FileWriter(indexFolderPath + metaFileName));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dumps the infobox information about the wiki pages into file "infobox.dat"
     * @param stringToWrite
     */
    public long dumpInfoInformation(String stringToWrite) {
        long old = infoFileSeekLocation;
        try {
            infoWriter.write(stringToWrite);
            infoFileSeekLocation += stringToWrite.getBytes().length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return old;
    }

    /**
     * Dumps the meta information about the wiki pages into file "metadata.dat"
     * @param wikiPage
     * @param infoboxSeekLocation
     */
    public void dumpMetaInformation(WikiPageConcise wikiPage, long infoboxSeekLocation) {
        try {
            metaWriter.write(wikiPage.getPageId() + ":" + infoboxSeekLocation + ":" + wikiPage.getPageTitle() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the buffered writer stream for the metadata.
     */
    public void close() {
        try {
            metaWriter.close();
            infoWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the word wordToWrite into temporaryfile number countTemporaryFile using buffered writer.
     * @param wordToWrite String that is to be written onto the file. {word:docId1:docId2}
     * @param countTemporaryFile The ith temporary file on which the wordToWrite is to be written.
     */
    public void writeDataToTemporaryFile(StringBuilder wordToWrite, int countTemporaryFile) {

        try {
            if ( temporaryFileWriter == null ) {
                temporaryFileWriter = new BufferedWriter(new FileWriter(indexFolderPath + temporaryFilePrefix + countTemporaryFile));
            }

            //System.out.println("Write : " + countTemporaryFile + " ->" + wordToWrite);
            temporaryFileWriter.write(new String(wordToWrite));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Dumps the data of temporary file to disk and closes the Buffered writer.
     */
    public void dumpTemporaryFileToDisk() {
        try {
            if ( temporaryFileWriter != null ) {
                temporaryFileWriter.close();
                temporaryFileWriter = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merges temporary files into a file named "index.idx" using external k-way merging.
     * @param countTemporaryFile Number of temporary files that were created during file creation phase.
     */
    public void mergeTemporaryFiles(int countTemporaryFile) throws IOException {

        if ( countTemporaryFile == 1 ) {
            splitIntoMultiple(countTemporaryFile);
            return;
        }

        availableFiles = countTemporaryFile;

        /* These are the files that will be created as a part of index files */
        String[] fileNames = {
                "indexa.idx" ,"indexb.idx" ,"indexc.idx" ,"indexd.idx" ,"indexe.idx" ,"indexf.idx" ,"indexg.idx" ,
                "indexh.idx" ,"indexi.idx" ,"indexj.idx" ,"indexk.idx" ,"indexl.idx" ,"indexm.idx" ,"indexn.idx" ,
                "indexo.idx" ,"indexp.idx" ,"indexq.idx" ,"indexr.idx" ,"indexs.idx" ,"indext.idx" ,"indexu.idx" ,
                "indexv.idx" ,"indexw.idx" ,"indexx.idx" ,"indexy.idx" ,"indexz.idx"
        };

        String[] sfileNames = {
                "sindexa.idx" ,"sindexb.idx" ,"sindexc.idx" ,"sindexd.idx" ,"sindexe.idx" ,"sindexf.idx" ,"sindexg.idx" ,
                "sindexh.idx" ,"sindexi.idx" ,"sindexj.idx" ,"sindexk.idx" ,"sindexl.idx" ,"sindexm.idx" ,"sindexn.idx" ,
                "sindexo.idx" ,"sindexp.idx" ,"sindexq.idx" ,"sindexr.idx" ,"sindexs.idx" ,"sindext.idx" ,"sindexu.idx" ,
                "sindexv.idx" ,"sindexw.idx" ,"sindexx.idx" ,"sindexy.idx" ,"sindexz.idx"
        };

        long fileSeeks[] = new long[fileNames.length];
        int fileSeekDictionary = 0;
        int wordCount[] = new int[sfileNames.length];

        File[] fileObjects = new File[countTemporaryFile];
        BufferedWriter[] writer = new BufferedWriter[fileNames.length];
        BufferedWriter[] swriter = new BufferedWriter[sfileNames.length];

        for ( int i = 0 ; i < fileNames.length ; i++ ) {
            writer[i] = new BufferedWriter(new FileWriter(indexFolderPath + fileNames[i]));
            swriter[i] = new BufferedWriter(new FileWriter(indexFolderPath + sfileNames[i]));
            fileSeeks[i] = 0;
            wordCount[i] = 0;
        }


        PriorityQueue<String> listOfWords = new PriorityQueue<String>();
        BufferedReader[] reader = new BufferedReader[countTemporaryFile+1];

        //String[] term = new String[countTemporaryFile+1];
        boolean[] lineRead = new boolean[countTemporaryFile+1];

        for(int i = 0 ; i < countTemporaryFile ; i++) {
            fileObjects[i] = new File(indexFolderPath + temporaryFilePrefix + i);
            reader[i] = new BufferedReader(new FileReader(fileObjects[i]));
            //term[i] = line[i].substring(line[i].indexOf(':'));

            /* Every term in the priority queu is like :
             *  <term>_<index>
             *      This index specifies which temporary file does this term belongs.
             */
            listOfWords.add(reader[i].readLine() + "_" + i);
        }

        while( availableFiles > 0 ) {

            String lineToWrite = getLineToWrite(listOfWords , reader);
            //System.out.println("Line to write : " + lineToWrite);

            if(lineToWrite == null)
                break;

            StringBuilder lineToWriteDictionaryBuilder = new StringBuilder();
            char startChar = lineToWrite.charAt(0);
            int index = ((int)startChar) - ((int)'a');
            if ( index < 26 ) {
                //System.out.println("Writing in dictionary : " + lineToWrite.substring(0,lineToWrite.indexOf(":")) + ":" + fileSeeks[index]);

                int lineLength = lineToWrite.length();
                int i = 0;
                for ( i = 0 ; i < lineLength ; i++ ) {
                    char currentChar = lineToWrite.charAt(i);
                    if ( currentChar == ':' )
                        break;
                    lineToWriteDictionaryBuilder.append(currentChar);
                }
                /* Till this point the lineToWriteDictionaryBuilder has the TERM */
                lineToWriteDictionaryBuilder.append(':');
                lineToWriteDictionaryBuilder.append(fileSeeks[index]);
                lineToWriteDictionaryBuilder.append('\n');

                String dictionaryWord = new String(lineToWriteDictionaryBuilder);

                dictionaryWriter.write(dictionaryWord);

                if ( wordCount[index] == 0 ) {
                    // Dump word to secondary file
                    StringBuilder secondaryWordBuilder = new StringBuilder();
                    secondaryWordBuilder.append(lineToWriteDictionaryBuilder.substring(0,lineToWriteDictionaryBuilder.indexOf(":")+1));
                    secondaryWordBuilder.append(fileSeekDictionary);
                    secondaryWordBuilder.append('\n');
                    swriter[index].write(new String(secondaryWordBuilder));
                    wordCount[index] = WORD_BUFFER_SIZE;
                }

                wordCount[index] --;
                fileSeekDictionary += (dictionaryWord.getBytes().length);


                lineToWriteDictionaryBuilder.setLength(0);

                for ( i ++ ; i < lineLength ; i++ ) {
                    lineToWriteDictionaryBuilder.append(lineToWrite.charAt(i));
                }

                /*
                 * TODO: Index Compression login goes here
                 */
                String posting = new String(lineToWriteDictionaryBuilder);
                writer[index].write(posting);
                writer[index].write('\n');
                fileSeeks[index] += (1+posting.getBytes().length);

                lineToWriteDictionaryBuilder.setLength(0);

            }
            lineToWriteDictionaryBuilder.setLength(0);

        }

        for ( int i = 0 ; i < fileNames.length ; i++ ) {
            writer[i].close();
            swriter[i].close();
        }
        for ( int i = 0 ; i < countTemporaryFile ; i++ ) {
            fileObjects[i].delete();
        }

        dictionaryWriter.close();
    }


    /**
     * Very rare situation when number of temporary files created is 1.
     * @param countTemporaryFile
     * @throws IOException
     */
    private void splitIntoMultiple(int countTemporaryFile) throws IOException {

        /* These are the files that will be created as a part of index files */
        String[] fileNames = {
                "indexa.idx" ,"indexb.idx" ,"indexc.idx" ,"indexd.idx" ,"indexe.idx" ,"indexf.idx" ,"indexg.idx" ,
                "indexh.idx" ,"indexi.idx" ,"indexj.idx" ,"indexk.idx" ,"indexl.idx" ,"indexm.idx" ,"indexn.idx" ,
                "indexo.idx" ,"indexp.idx" ,"indexq.idx" ,"indexr.idx" ,"indexs.idx" ,"indext.idx" ,"indexu.idx" ,
                "indexv.idx" ,"indexw.idx" ,"indexx.idx" ,"indexy.idx" ,"indexz.idx"
        };

        BufferedWriter[] writer = new BufferedWriter[fileNames.length];
        int fileSeeks[] = new int[fileNames.length];

        for ( int i = 0 ; i < fileNames.length ; i++ ) {
            writer[i] = new BufferedWriter(new FileWriter(indexFolderPath + fileNames[i]));
            fileSeeks[i] = 0;
        }

        File fileObject = new File(indexFolderPath + temporaryFilePrefix + "0");
        BufferedReader reader = new BufferedReader(new FileReader(fileObject));

        String lineToWrite = null;
        StringBuilder lineToWriteDictionaryBuilder = new StringBuilder();
        while( (lineToWrite = reader.readLine()) != null ) {
            char startChar = lineToWrite.charAt(0);
            int index = ((int)startChar) - ((int)'a');
            if ( index < 26 ) {
                //System.out.println("Writing in dictionary : " + lineToWrite.substring(0,lineToWrite.indexOf(":")) + ":" + fileSeeks[index]);

                int lineLength = lineToWrite.length();
                int i = 0;
                for ( i = 0 ; i < lineLength ; i++ ) {
                    char currentChar = lineToWrite.charAt(i);
                    if ( currentChar == ':' )
                        break;
                    lineToWriteDictionaryBuilder.append(currentChar);
                }
                /* Till this point the lineToWriteDictionaryBuilder has the TERM */
                lineToWriteDictionaryBuilder.append(':');
                lineToWriteDictionaryBuilder.append(fileSeeks[index]);
                lineToWriteDictionaryBuilder.append('\n');

                dictionaryWriter.write(new String(lineToWriteDictionaryBuilder));
                lineToWriteDictionaryBuilder.setLength(0);

                for ( i ++ ; i < lineLength ; i++ ) {
                    lineToWriteDictionaryBuilder.append(lineToWrite.charAt(i));
                }

                String posting = new String(lineToWriteDictionaryBuilder);
                writer[index].write(posting);
                writer[index].write('\n');
                fileSeeks[index] += (1+posting.getBytes().length);

                lineToWriteDictionaryBuilder.setLength(0);

            }
            lineToWriteDictionaryBuilder.setLength(0);

        }

        for ( int i = 0 ; i < fileNames.length ; i++ ) {
            writer[i].close();
        }
        fileObject.delete();
        dictionaryWriter.close();
    }

    private String getLineToWrite(PriorityQueue<String> listOfWords, BufferedReader[] reader) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        if(listOfWords.isEmpty())
            return null;

        String vocabularyPosting = listOfWords.remove();

        if(listOfWords.isEmpty())
            return vocabularyPosting.substring(0,vocabularyPosting.indexOf("_"));

        String currentTerm = vocabularyPosting.substring(0,vocabularyPosting.indexOf(':'));
        int index = Integer.parseInt(vocabularyPosting.substring(vocabularyPosting.indexOf("_")+1));

        String lineRead = reader[index].readLine();
        if ( lineRead == null )
            availableFiles --;
        else
            listOfWords.add( lineRead + "_" + index);

        stringBuilder.append(vocabularyPosting.substring(0,vocabularyPosting.indexOf("_")));

        while(!listOfWords.isEmpty() && availableFiles > 0 ) {

            //System.out.println("Available file : " + availableFiles );

            /*
             * If peeked term is not same as current term then break the loop
             */
            String peekedPostingPlusIndex = listOfWords.peek();
            String peekedPosting = peekedPostingPlusIndex.substring(0,peekedPostingPlusIndex.indexOf('_'));
            String peekedTerm = peekedPosting.substring(0,peekedPosting.indexOf(':'));
            int peekedIndex = Integer.parseInt(peekedPostingPlusIndex.substring(peekedPostingPlusIndex.indexOf('_') + 1));

            if ( !peekedTerm.equals(currentTerm) )
                break;

            stringBuilder.append( peekedPosting.substring(peekedPosting.indexOf(':')) );

            lineRead = reader[peekedIndex].readLine();
            if ( lineRead == null )
                availableFiles --;
            else {
                listOfWords.add( lineRead + "_" + peekedIndex);
                listOfWords.remove();
            }

        }

        return new String(stringBuilder);

    }
}
