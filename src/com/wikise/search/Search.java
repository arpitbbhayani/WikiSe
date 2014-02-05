package com.wikise.search;

import com.wikise.util.Classifiers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Search {

    private FileReadIO fileReadIO = null;
    private String indexFolderPath = null;
    private TreeMap<String,Integer> pageMetadataMap = null;

    public Search(FileReadIO fileReadIO , String indexFolderPath) {
        this.fileReadIO = fileReadIO;
        this.indexFolderPath = indexFolderPath;
    }

    private ArrayList<String> search(String s, FileReadIO fileReadIO) {

        System.out.println("Search : " + s);
        return fileReadIO.getPostingList(s);

    }

    public static void main(String[] args) {

        String indexFolderPath = "/media/devilo/GaMeS aNd SeTuPs/data/";

        /*
         *  HashedFileReadIO hashedFileReadIO = new HashedFileReadIO(indexFolderPath);
         *  HashedFileSecondaryReadIO fileSecondaryReadIO = new HashedFileSecondaryReadIO(indexFolderPath);
         */

        Classifiers.initialize();
        FileReadIO fileReadIO = new FileReadIO(indexFolderPath);
        Search wikiSearch = new Search(fileReadIO , indexFolderPath);

        Scanner scanner = new Scanner(System.in);

        long startTimeMetadata = System.currentTimeMillis();

        wikiSearch.loadPageMetadata(indexFolderPath);

        long stopTimeMetadata = System.currentTimeMillis();
        float elapsedTimeMetadata = (stopTimeMetadata - startTimeMetadata)/1000f;

        System.out.println("Loading Metadata : " + elapsedTimeMetadata);

        for ( int i = 0 ; i < 2 ; i++ ) {

            String searchQuery = scanner.nextLine();

            long startTime = System.currentTimeMillis();

            /*
             * Old code :
             * TreeSet<Integer> listOfDocId = search(searchQuery.toLowerCase() , hashedFileReadIO , fileSecondaryReadIO);
             * formatOutput(listOfDocId);
             */

            ArrayList<String> listDocIds = wikiSearch.search(searchQuery, fileReadIO);
            int k = 0;

            for ( String entity : listDocIds ) {

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

                try {
                    //System.out.println("ID " + docId + " - " + wikiSearch.getWikiPageTitle(new String(docId)) + " term frequency = " + termFrequency + " and bit = " + bitRepresentation);
                    //if ( new String(docId).equals("19991266")) {
                        System.out.println("ID " + docId + " - " + wikiSearch.getWikiPageTitle(new String(docId)) + " term frequency = " + termFrequency + " and bit = " + bitRepresentation);
                    //}
                    //wikiSearch.getWikiPageTitle(new String(docId));

                    //wikiSearch.getWikiPageTitle(new String(docId));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long stopTime = System.currentTimeMillis();
            float elapsedTime = (stopTime - startTime)/1000f;

            System.out.println(elapsedTime);

        }

    }

    /**
     * returns Page Title given docID.
     * @param docId Wiki Document ID.
     */
    private String getWikiPageTitle(String docId) throws IOException{

        /* From memory get the offset */
        int docIdToBeSearched = Integer.parseInt(docId);
        int prevDocId = 0 , prevOffset = 0;

        for ( String docIdCurrent : this.pageMetadataMap.keySet() ) {
            int currentDocId = Integer.parseInt(docIdCurrent);
            int currentOffset = this.pageMetadataMap.get(docIdCurrent);
            if ( docIdToBeSearched >= prevDocId && docIdToBeSearched < currentDocId ) {
                break;
            }
            prevDocId = currentDocId;
            prevOffset = currentOffset;
        }

        /* Go to the file ssmetadata.dat at offset prevOffset */
        int offset = prevOffset;
        prevDocId = 0; prevOffset = 0;

        RandomAccessFile randomAccessFile = new RandomAccessFile(this.indexFolderPath + "meta/ssmetadata.dat" , "r");
        randomAccessFile.seek(offset);

        String line = null;
        while ( (line = randomAccessFile.readLine()) != null ) {

            int index = line.indexOf(':');
            int currentDocId = Integer.parseInt( line.substring(0,index) );
            int currentOffset = Integer.parseInt( line.substring(index+1) );

            if ( docIdToBeSearched >= prevDocId && docIdToBeSearched < currentDocId ) {
                break;
            }
            prevDocId = currentDocId;
            prevOffset = currentOffset;

        }
        randomAccessFile.close();

        /* Go to the file smetadata.dat at offset = offset */
        offset = prevOffset;
        prevOffset = 0;

        randomAccessFile = new RandomAccessFile(this.indexFolderPath + "meta/smetadata.dat" , "r");
        randomAccessFile.seek(offset);

        line = null;
        while ( (line = randomAccessFile.readLine()) != null ) {
            /* Check for equality and get the offset */

            int i = 0 , j = 0;
            int lineLength = line.length();
            int sLength = docId.length();

            while ( i < lineLength && j < sLength && line.charAt(i) == docId.charAt(j) ) {
                i++;
                j++;
            }

            if ( i < lineLength && j == sLength && line.charAt(i) == ':' ) {
                /* Term matched */
                prevOffset = 0;
                for ( i++; i < lineLength ; i++ ) {
                    prevOffset = prevOffset * 10 + ((int)line.charAt(i)- (int)'0');
                }
                break;
            }

        }

        randomAccessFile.close();

        /* Go to the file pagemetadata.dat at offset = offset */
        offset = prevOffset;

        randomAccessFile = new RandomAccessFile(this.indexFolderPath + "meta/pagemetadata.dat" , "r");
        randomAccessFile.seek(offset);

        line = randomAccessFile.readLine();

        randomAccessFile.close();
        return line;
    }

    /**
     * Reads the metadata and create an online index.
     * @param indexFolderPath path of the index folder.
     */
    private TreeMap<String , Integer> loadPageMetadata(String indexFolderPath) {

        pageMetadataMap = new TreeMap<String, Integer>();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(indexFolderPath + "meta/sssmetadata.dat"));

            StringBuilder docId = new StringBuilder();
            int metaDataOffset = 0;

            String line = bufferedReader.readLine();
            while ( line != null ) {

                int i = 0;
                int length = line.length();

                for ( ; i < length ; i++ ) {
                    char currentChar = line.charAt(i);
                    if ( currentChar == ':' ) {
                        break;
                    }
                    docId.append(currentChar);
                }

                for ( i++ ; i < length ; i++ ) {
                    metaDataOffset = metaDataOffset * 10 + ((int) line.charAt(i) - (int) '0');
                }
                pageMetadataMap.put(new String(docId), Integer.valueOf(metaDataOffset));
                docId.setLength(0);
                metaDataOffset = 0;

                line = bufferedReader.readLine();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return  pageMetadataMap;

    }

}