package com.wikise.search;

import com.wikise.util.Classifiers;
import com.wikise.util.CompressionDecompression;

import java.io.*;
import java.util.*;

/**
 * Created by Arpit Bhayani on 31/1/14.
 */
public class FileReadIO {

    CompressionDecompression compressionDecompression = null;
    private String dictionaryFileName = "meta/ndictionary.dat";
    private String infoFileName = "meta/info.dat";

    private String[] fileNames = {
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

    private String indexFolderPath = null;

    private BufferedReader dictionaryReader = null;

    public FileReadIO(String folderPath) {

        compressionDecompression = new CompressionDecompression();
        if ( folderPath.charAt(folderPath.length()-1) != '/' ) {
            this.indexFolderPath = folderPath + '/';
        }
        else {
            this.indexFolderPath = folderPath;
        }

    }

    /**
     * Main search function. This function is exposed to the rest of the world.
     * Gets a posting list for a search query (can have multiple words).
     *
     *
     * @param s search query
     * @param searchFieldsForTerms
     * @return Posting list
     */
    public ArrayList<String> getPostingList(ArrayList<HashSet<String>> s, HashMap<String, Integer> searchFieldsForTerms) {

        try {
            /* In how many words does this doc has appeared ( docId -> count ) */
            HashMap<String , Integer> documentToWord = new HashMap<String, Integer>();
            HashMap<String , Double> documentToTfIdf = new HashMap<String, Double>();

            for ( int i = 0 ; i < 26 ; i++ ) {
                HashSet<String> setOfWords = s.get(i);
                for ( String searchTerm : setOfWords ) {
                    //System.out.println(getPostingsForSingleTerm(searchTerm));

                    Integer requiredFields = searchFieldsForTerms.get(searchTerm);

                    if ( requiredFields == null )
                        requiredFields = 40;    // Title and Body

                    //System.out.println("Required fields for " + searchTerm + " are : " + requiredFields);

                    ArrayList<String> tempList = new ArrayList<String>();
                    //System.out.println("Getting posting list for " + searchTerm);
                    tempList = getPostingsForSingleTerm(searchTerm);
                    //System.out.println("Length is : " + tempList.size());
                    for ( String entity : tempList ) {

                        int length = entity.length();
                        StringBuilder docId = new StringBuilder();
                        int bitRepresentation = 0;
                        int termFrequency = 0;

                        int j = 0;
                        char currentChar;
                        for ( ; (currentChar = entity.charAt(j)) != '$' ; j++ ) {
                            docId.append(currentChar);
                        }
                        for ( j++ ; (currentChar = entity.charAt(j)) != '$' ; j++ ) {
                            bitRepresentation  = bitRepresentation * 10 + ((int)currentChar - (int)'0');
                        }
                        for ( j++ ; j < length && (currentChar = entity.charAt(j)) != '$' ; j++ ) {
                            termFrequency  = termFrequency * 10 + ((int)currentChar - (int)'0');
                        }

                        String docIdStr = new String(docId);
                        if ( ((requiredFields & 32) != 0 && (bitRepresentation & 32) != 0) ||
                                ((requiredFields & 16) != 0 && (bitRepresentation & 16) != 0) ||
                                ((requiredFields & 8) != 0 && (bitRepresentation & 8) != 0) ||
                                ((requiredFields & 4) != 0 && (bitRepresentation & 4) != 0)) {

                            Double oldTfidf = documentToTfIdf.get(docId);
                            if ( oldTfidf == null )
                                oldTfidf = Double.valueOf(0);
                            Double newTfIdf = tfidf(termFrequency , tempList.size() );

                            if ( newTfIdf > oldTfidf )
                                documentToTfIdf.put(docIdStr , newTfIdf );

                            Integer count = documentToWord.get(docIdStr);
                            if ( count == null ) {
                                count = 0;
                            }

                            ++count;
                            documentToWord.put( docIdStr , count);
                        }
                    }
                }
            }

            //System.out.println("Length : " + documentToTfIdf.size());
            ArrayList<String> docIds = new ArrayList<String>();
            int K = 15;

            if ( documentToTfIdf.size() == 0 ) {
                return docIds;
            }

            double maxtfidf = -1;
            String maxKey = null;

            for ( int i = 0 ; i < K ; i++ ) {

                for ( String key : documentToTfIdf.keySet() ) {

                    double tfidf = documentToTfIdf.get(key);

                    if ( tfidf > maxtfidf ) {
                        maxtfidf = tfidf;
                        maxKey = key;
                    }

                }

                if ( maxtfidf == -1 ) {
                    break;
                }
                docIds.add(maxKey);
                //System.out.println("Key : " + maxKey + " and " + maxtfidf);
                documentToTfIdf.put(maxKey , Double.valueOf(-1));
                maxtfidf = -1;

            }

            return docIds;


            //for ( String docIdStr : documentToTfIdf.keySet() ) {
            //    System.out.println(docIdStr + "-" + documentToTfIdf.get(docIdStr));
            //}

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private double tfidf(int tf, int df) {

        return (1+Math.log10(tf)) * Math.log10(14128976/(float)df);

    }

    private ArrayList<String> getPostingsForSingleTerm(String singleTerm) throws IOException {

        String processedTerm = Classifiers.getStemmedWord(singleTerm);

        if ( singleTerm.length() == 0 )
            return new ArrayList<String>();

        char startChar = singleTerm.charAt(0);
        int index = (int)startChar - (int)'a';

        if ( index < 0 || index > 25 )
            return new ArrayList<String>();

        /* Read from sindexa.idx and get the offset in the file dictionary.dat */
        long offsetInDictionary = getOffsetInDictionary(processedTerm);

        //System.out.println("Offset in ndictionary.dat file : " + offsetInDictionary);

        if ( offsetInDictionary == -1 )
            return new ArrayList<String>();

        /* Go to the dictionary.dat at the given offset,
          searches the term in dictionary.dat, gets the offset -> this offset is in the indexa.idx file.
          term.
          The indexa.idx file where the posting list is stored.
        */
        return getPostingsListForTerm(processedTerm, offsetInDictionary);

    }

    /**
     * returns the posting list given the offset in the dictionary and processed term..
     * @param processedTerm
     * @param offsetInDictionary
     * @return
     * @throws IOException
     */
    private ArrayList<String> getPostingsListForTerm(String processedTerm, long offsetInDictionary) throws  IOException {

        ArrayList<String> postingList = new ArrayList<String>();

        char startChar = processedTerm.charAt(0);
        int index = (int)startChar - (int)'a';

        if ( index < 0 || index > 25 )
            return new ArrayList<String>();

        /* Read the dictionary.dat file and get the offset in the indexa.idx */

        String offsetCombined = getOffsetInInvertedIndexFile(offsetInDictionary, processedTerm);
        //System.out.println("Offset combined : " + offsetCombined);
        int index_ = offsetCombined.indexOf('_');

        if ( index_ == 0 ) {
            return postingList;
        }

        long offset = Long.parseLong( offsetCombined.substring(0 , index_ ));
        long offsetNextWord = Long.parseLong( offsetCombined.substring(index_ + 1));
        long lengthToBeRead = offsetNextWord - offset;
        if ( offsetNextWord == -1 ) {
            lengthToBeRead = -1;
        }


        if ( offset == -1 ) {
            return postingList;
        }

        /* Read the indexa.idx file and go to offset offsetIn_indexa_File and get the posting list. */
        RandomAccessFile randomAccessFile = new RandomAccessFile(indexFolderPath + "nindex/n" + fileNames[index] , "r");

        //System.out.println("Reading file " + indexFolderPath + "nindex/n" + fileNames[index]);
        //System.out.println("Going at offset in main posting file : " + offset);

        //System.out.println("Length to be read = " + lengthToBeRead);
        //System.out.println("Length of file : " + randomAccessFile.length());
        //System.out.println("Offset = " + offset);

        if ( lengthToBeRead < 1 ) {
            //
            // System.out.println("Last word so reading full file from offset offset");
            lengthToBeRead = randomAccessFile.length() - offset;
        }

        //System.out.println("Length to be read = " + lengthToBeRead);

        byte[] lineBytes = new byte[(int)lengthToBeRead];
        //randomAccessFile.read(line, (int) offset, (int)lengthToBeRead - 1);
        randomAccessFile.seek(offset);
        randomAccessFile.readFully(lineBytes);
        //System.out.println(compressionDecompression.decompress(line));

        String line = compressionDecompression.decompress(lineBytes);
        String[] splitArray = line.split(":");

        for ( String split : splitArray ) {
            postingList.add(split);
        }

        return postingList;
    }

    /**
     * Returns the offset in the inverted index given the offset in dictionary.
     *
     * @param offsetInDictionary
     * @param s
     * @return OFFSET_NEXTOFFSET
     * @throws IOException
     */
    private String getOffsetInInvertedIndexFile(long offsetInDictionary, String s) throws IOException {

        RandomAccessFile randomAccessFile = new RandomAccessFile(indexFolderPath + dictionaryFileName , "r");
        randomAccessFile.seek(offsetInDictionary);

        String line = null;

        boolean hasNextWord = false;

        StringBuilder offsetWord = new StringBuilder();
        //StringBuilder offsetNextWord = new StringBuilder("-1");

        while ( (line = randomAccessFile.readLine()) != null && line.charAt(0) == s.charAt(0) ) {
            int i = 0 , j = 0;
            int lineLength = line.length();
            int sLength = s.length();

            while ( i < lineLength && j < sLength && line.charAt(i) == s.charAt(j) ) {
                i++;
                j++;
            }

            if ( i < lineLength && j == sLength && line.charAt(i) == ':' ) {
                /* Term matched */

                //offsetWord = 0;
                for ( i++; i < lineLength ; i++ ) {
                    //offsetWord = offsetWord * 10 + ((int)line.charAt(i)- (int)'0');
                    offsetWord.append(line.charAt(i));
                }
                break;
            }
        }

        offsetWord.append('_');

        if ( (line = randomAccessFile.readLine()) != null ) {



            int i = 0;
            int lineLength = line.length();

            while ( i < lineLength && line.charAt(i) != ':' ) {
                i++;
            }

            if ( i < lineLength && line.charAt(i) == ':' ) {
                hasNextWord = true;
                /* Term matched */
                //offsetNextWord = 0;

                for ( i++; i < lineLength ; i++ ) {
                    //offsetNextWord = offsetNextWord * 10 + ((int)line.charAt(i)- (int)'0');
                    offsetWord.append(line.charAt(i));
                }
            }
        }

        if ( hasNextWord == false ) {
            offsetWord.append("-1");
        }
        return new String(offsetWord);

    }

    /**
     * Reads secondary index for the word and gets the offset in the Dictionary index.
     * This function is called to get the offset in dictonary.
     * This offset is of the file dictionary.dat which is stored in sindexa.dat.
     * @param singleTerm
     * @return offset in the primary index.
     */
    private long getOffsetInDictionary(String singleTerm) throws IOException{

        char startChar = singleTerm.charAt(0);
        int index = (int)startChar - (int)'a';

        if ( index < 0 || index > 25 )
            return -1;


        BufferedReader secondaryReader = new BufferedReader(new FileReader(indexFolderPath + "nsindex/n" + sfileNames[index]));
        String line = null;

        line = secondaryReader.readLine();

        long prevOffset = Long.parseLong(line.substring(line.indexOf(':')+1));

        while ( (line = secondaryReader.readLine()) != null ) {

            String term = line.substring(0,line.indexOf(':'));
            long offset = Long.parseLong(line.substring(line.indexOf(':')+1));

            if ( term.compareTo(singleTerm) > 0 ) {
                return prevOffset;
            }
            prevOffset = offset;
        }

        return prevOffset;
    }

    /**
     * Gets the posting list for a term s.
     * @param s search term (stemmed)
     * @return Posting list for a single term.
     * @throws IOException
     */
    private ArrayList<String> getPostingListForTerm(String s) throws  IOException {

        ArrayList<String> postingList = new ArrayList<String>();

        //System.out.println("Stemmed term : " + processedTerm);
        long offsetInInvertedIndex = getOffsetInInvertedIndex(s);

        if ( offsetInInvertedIndex == -1 ) {
            //System.out.println("Term not found");
            return postingList;
        }
        else {
            //System.out.println("Term found at dictionary offset : " + offsetInInvertedIndex);

            int index = ((int)s.charAt(0)) - ((int)'a');
            if ( index < 0 || index > 25 ) {
                /* Non-english words */
                System.out.println("Out of bound !!");
                return postingList;
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(indexFolderPath + "nindex/n" + fileNames[index] , "r");

            randomAccessFile.seek(offsetInInvertedIndex);

            String line = randomAccessFile.readLine();
            String[] splitArray = line.split(":");

            for ( String split : splitArray ) {
                postingList.add(split);
            }

        }

        return postingList;
    }

    /**
     * This method searches the word in the dictionary file and if found returns
     * the corresponding offset in the index file.
     * @param s Search term
     * @return Offset in index file.
     */
    private long getOffsetInInvertedIndex(String s) throws IOException {

        dictionaryReader = new BufferedReader(new FileReader(indexFolderPath + dictionaryFileName));
        String line = null;

        while ( (line = dictionaryReader.readLine()) != null ) {
            int i = 0 , j = 0;
            int lineLength = line.length();
            int sLength = s.length();

            while ( i < lineLength && j < sLength && line.charAt(i) == s.charAt(j) ) {
                i++;
                j++;
            }

            if ( i < lineLength && j == sLength && line.charAt(i) == ':' ) {
                /* Term matched */
                long offset = 0;
                for ( i++; i < lineLength ; i++ ) {
                    offset = offset * 10 + ((int)line.charAt(i)- (int)'0');
                }
                return offset;
            }

        }

        return -1;
    }


    /**
     * This method sequentially searches using HashedIO i.e. Trie.
     * @param startChar
     * @param listOfSeeks
     * @return
     */
    public TreeSet<Integer> readData(char startChar , TreeSet<Integer> listOfSeeks) {

        if ( listOfSeeks == null )
            return new TreeSet<Integer>();

        TreeSet<Integer> pageIds = new TreeSet<Integer>();
        int index = ((int)startChar) - ((int)'a');

        RandomAccessFile randomAccessFile = null;

        try {

            randomAccessFile = new RandomAccessFile(indexFolderPath + "nindex/n" + fileNames[index] , "r");

            StringBuffer stringBuffer = new StringBuffer();

            for ( long seekLocation : listOfSeeks ) {

                randomAccessFile.seek(seekLocation);

                String line = randomAccessFile.readLine();
                String[] splitted = line.split(":");

                for ( String split : splitted ) {
                    if ( Character.isDigit(split.charAt(0)) )
                        pageIds.add(new Integer(split));
                }

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pageIds;
    }

}
