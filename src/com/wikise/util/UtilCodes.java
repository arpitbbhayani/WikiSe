package com.wikise.util;

import java.io.*;

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

    public static void main(String[] args) {

        System.out.println("Creating sMetaDatafile from metadata...");

        createSMetadata("/media/devilo/GaMeS aNd SeTuPs/data/meta/metadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/smetadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/ssmetadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/sssmetadata.dat" ,
                "/media/devilo/GaMeS aNd SeTuPs/data/meta/pagemetadata.dat");

        System.out.println("sMetadata file created !");

    }

}
