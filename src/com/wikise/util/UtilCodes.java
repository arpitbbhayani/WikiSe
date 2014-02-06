package com.wikise.util;

import javax.print.attribute.standard.Compression;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by Arpit Bhayani on 5/2/14.
 */
public class UtilCodes {

    /**
     * From metedataFilePath ( old meta data file)
     * this function creates
     * 1. New meta data file -> pageMetadataFilePath ( Infobox offset and title )
     * 2. s metadata file -> docId and offset.
     * 3. ss metadata file -> docId and offset to s metadata file. ( per 100 words )
     * 4. sss metadata file -> docId and offset to ss metadata file ( per 100 words )
     *
     * @param metedataFilePath
     * @param sMetadataFilePath
     * @param ssMetadataFilePath
     * @param sssMetadataFilePath
     * @param pageMetadataFilePath
     */
    public static void createSMetadata( String metedataFilePath , String sMetadataFilePath , String ssMetadataFilePath , String sssMetadataFilePath , String pageMetadataFilePath) {

        BufferedReader reader = null;
        BufferedWriter swriter = null;
        BufferedWriter sswriter = null;
        BufferedWriter ssswriter = null;
        BufferedWriter newMetaWrite = null;

        try {

            reader = new BufferedReader(new FileReader(metedataFilePath));
            swriter = new BufferedWriter(new FileWriter(sMetadataFilePath));
            sswriter = new BufferedWriter(new FileWriter(ssMetadataFilePath));
            ssswriter = new BufferedWriter(new FileWriter(sssMetadataFilePath));
            newMetaWrite = new BufferedWriter(new FileWriter(pageMetadataFilePath));

            String str = reader.readLine();

            /*
             * Store every line from metadata to sMetadata
             * Store every 100th line from sMetadata to ssMetadata
             * Store every 100th line from ssMetadata to sssMetadata
             */
            int ssWordLimit = 300 , sssWordLimit = 300;
            int ssWordCount = 0 , sssWordCount = 1;

            long sfileSeek = 0 , ssfileSeek = 0 , sssfileSeek = 0;
            long sfileSeekOld = 0 , ssfileSeekOld = 0 , sssfileSeekOld = 0;

            while ( str != null ) {

                sfileSeekOld = sfileSeek;
                ssfileSeekOld = ssfileSeek;
                sssfileSeekOld = sssfileSeek;

                int index = str.indexOf(':');
                String docID = str.substring(0, index);

                String tempString = docID + ":" + sfileSeekOld + "\n";
                swriter.write(tempString);
                ssfileSeek += tempString.getBytes().length;

                if ( ssWordCount == 0 ) {
                    ssWordCount = ssWordLimit;
                    String lineToWriteSSFile = docID + ":" + ssfileSeekOld + "\n";
                    sswriter.write(lineToWriteSSFile);
                    sssfileSeek += lineToWriteSSFile.getBytes().length;

                    sssWordCount --;
                }

                if ( sssWordCount == 0 ) {
                    sssWordCount = sssWordLimit;
                    String lineToWriteSSSFile = docID + ":" + sssfileSeekOld + "\n";
                    ssswriter.write(lineToWriteSSSFile);
                }

                ssWordCount --;

                String newString = str.substring(index + 1);
                newMetaWrite.write(newString+"\n");
                sfileSeek += newString.getBytes().length + 1;

                str = reader.readLine();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            try {
                reader.close();
                swriter.close();
                sswriter.close();
                ssswriter.close();
                newMetaWrite.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static String createCompressedIndex ( String indexFolderPath ) throws IOException {

        int WORD_BUFFER_SIZE = 500;

        String dictionaryFileName = "meta/dictionary.dat";
        String ndictionaryFileName = "meta/ndictionary.dat";

        String[] nfileNames = {
                "nindexa.idx" ,"nindexb.idx" ,"nindexc.idx" ,"nindexd.idx" ,"nindexe.idx" ,"nindexf.idx" ,"nindexg.idx" ,
                "nindexh.idx" ,"nindexi.idx" ,"nindexj.idx" ,"nindexk.idx" ,"nindexl.idx" ,"nindexm.idx" ,"nindexn.idx" ,
                "nindexo.idx" ,"nindexp.idx" ,"nindexq.idx" ,"nindexr.idx" ,"nindexs.idx" ,"nindext.idx" ,"nindexu.idx" ,
                "nindexv.idx" ,"nindexw.idx" ,"nindexx.idx" ,"nindexy.idx" ,"nindexz.idx"
        };

        String[] nsfileNames = {
                "nsindexa.idx" ,"nsindexb.idx" ,"nsindexc.idx" ,"nsindexd.idx" ,"nsindexe.idx" ,"nsindexf.idx" ,"nsindexg.idx" ,
                "nsindexh.idx" ,"nsindexi.idx" ,"nsindexj.idx" ,"nsindexk.idx" ,"nsindexl.idx" ,"nsindexm.idx" ,"nsindexn.idx" ,
                "nsindexo.idx" ,"nsindexp.idx" ,"nsindexq.idx" ,"nsindexr.idx" ,"nsindexs.idx" ,"nsindext.idx" ,"nsindexu.idx" ,
                "nsindexv.idx" ,"nsindexw.idx" ,"nsindexx.idx" ,"nsindexy.idx" ,"nsindexz.idx"
        };

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


        /* Change Primary Index */

        /* Read indexa.idx -> for each posting list compress it and then dump it to nindexa.idx */
        BufferedReader dictionaryReader = new BufferedReader(new FileReader( indexFolderPath + dictionaryFileName));
        BufferedWriter ndictionaryWriter = new BufferedWriter(new FileWriter(indexFolderPath + ndictionaryFileName));

        String line = null , dline = null;
        CompressionDecompression compressUtil = new CompressionDecompression();

        long seekLocation = 0 , seekLocationNsFile = 0;
        int currentFileNumber = 0;

        while ( currentFileNumber < fileNames.length ) {

            System.out.println("Seek location at the begining of file " + currentFileNumber + " is : " + seekLocationNsFile);
            int wordCount = 0;
            seekLocation = 0;

            BufferedReader bufferedReader = new BufferedReader(new FileReader( indexFolderPath + fileNames[currentFileNumber]));
            DataOutputStream nindexWriter = new DataOutputStream(new FileOutputStream(indexFolderPath + "nindex/" + nfileNames[currentFileNumber] ));
            BufferedWriter nsindexWriter = new BufferedWriter(new FileWriter(indexFolderPath + "nsindex/" + nsfileNames[currentFileNumber] ));

            while ((line = bufferedReader.readLine()) != null) {

                /* get the term */

                dline = dictionaryReader.readLine();

                int index = dline.indexOf(':');
                String term = dline.substring(0,index);

                /* line is the posting list , so compress it.*/
                byte[] compress = compressUtil.compress(line);

                nindexWriter.write(compress);
                nindexWriter.write('\n');

                String tempString = term + ":" + seekLocation + "\n";
                ndictionaryWriter.write(tempString);

                if ( wordCount == 0 ) {
                    nsindexWriter.write(term + ":" + seekLocationNsFile + "\n");
                    wordCount = WORD_BUFFER_SIZE;
                }

                wordCount --;
                seekLocationNsFile += (tempString.getBytes().length);
                seekLocation += (compress.length + 1);
            }

            nindexWriter.close();
            nsindexWriter.close();
            bufferedReader.close();

            currentFileNumber ++;

            /* Change Secondary Index */

            /* Read ndictionary line by line, get the for every 500th word dump it to nsindexa.dat */

            /* Read fine byte by byte */
            /*byte[] byteArray = new byte[21];
            BufferedReader reader = new BufferedReader(new FileReader(indexFolderPath + "nindex/" + nfileNames[0] ));

            FileChannel fileChannel = new RandomAccessFile(indexFolderPath + "nindex/" + nfileNames[0], "r").getChannel();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            System.out.println("Decompressed : " + compressUtil.decompress(buffer));

            reader.close();
            fileChannel.close();
            */
        }

        dictionaryReader.close();
        ndictionaryWriter.close();

        return null;

    }

    public static void main(String[] args) {

        System.out.println("Creating new index from metadata...");

        createSMetadata("/media/devilo/GaMeS aNd SeTuPs/data/meta/metadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/smetadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/ssmetadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/sssmetadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/pagemetadata.dat");


        /*try {
            createCompressedIndex("/media/devilo/GaMeS aNd SeTuPs/data/");
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/

        /*CompressionDecompression compressionDecompression = new CompressionDecompression();
        byte[] array = compressionDecompression.compress("91");
        System.out.println("Compressed : " + new String(array));
        String asciiText = new String(array);
        byte[] array1 = asciiText.getBytes(Charset.forName("UTF-8"));
        String arrayStr = compressionDecompression.decompress(array1);
        System.out.println("Deompressed : " + arrayStr);
        */

        System.out.println("sMetadata index created !");

    }

}
