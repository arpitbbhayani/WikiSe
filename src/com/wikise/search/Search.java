package com.wikise.search;

import com.wikise.util.Classifiers;

import java.io.*;
import java.util.*;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Search {

    private String indexFolderPath = null;
    private TreeMap<String,Integer> pageMetadataMap = null;
    private FileReadIO fileReadIO = null;

    public Search(FileReadIO fileReadIO , String indexFolderPath) {
        this.indexFolderPath = indexFolderPath;
        this.fileReadIO = fileReadIO;
    }

    private ArrayList<String> search(int numberOfSearchTerms , ArrayList<HashSet<String>> searchTermMap, HashMap<String, Integer> searchFieldsForTerms) {

        if ( numberOfSearchTerms == 0 ) {
            return new ArrayList<String>();
        }

        ArrayList<String> docIds = fileReadIO.getPostingList(searchTermMap, searchFieldsForTerms);

        return docIds;
    }

    public static int editDistance(String str1,String str2) {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= str2.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= str1.length(); i++)
            for (int j = 1; j <= str2.length(); j++)
                distance[i][j] = Math.min(Math.min(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1),
                        distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

        return distance[str1.length()][str2.length()];
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
     */
    private TreeMap<String , Integer> loadPageMetadata() {

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

    public static void main(String[] args) {

        int GEOBOX = 1 ;    // 6th bit from LSB
        int CATEGORY = 16 ;    // 5th bit from LSB
        int TITLE = 32 ;        // 4th bit from LSB
        int BODY = 8 ;         // 3th bit from LSB
        int LINKS = 4 ;        // 2th bit from LSB
        int INFOBOX = 2 ;      // 1th bit from LSB

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

        System.out.println("Meta loading ...");

        wikiSearch.loadPageMetadata();
        Classifiers.fillUnrelatedDocuments(indexFolderPath);
        //fileReadIO.loadCache();

        long stopTimeMetadata = System.currentTimeMillis();
        float elapsedTimeMetadata = (stopTimeMetadata - startTimeMetadata)/1000f;

        System.out.println("Meta load completed ..." + elapsedTimeMetadata + " s");

        ArrayList<HashSet<String>> searchTermMap = new ArrayList<HashSet<String>>();
        for ( int j = 0 ; j < 26 ; j++ ) {
            searchTermMap.add(new HashSet<String>());
        }
        HashMap<String , Integer> searchFieldsForTerms = new HashMap<String, Integer>();
        for ( int i = 0 ; i < 100 ; i++ ) {

            String searchQuery = scanner.nextLine();

            long startTime = System.currentTimeMillis();

            /*
             * Old code :
             * TreeSet<Integer> listOfDocId = search(searchQuery.toLowerCase() , hashedFileReadIO , fileSecondaryReadIO);
             * formatOutput(listOfDocId);
             */

            for ( int j = 0 ; j < 26 ; j++ ) {
                searchTermMap.get(j).clear();
            }
            searchFieldsForTerms.clear();

            String infoboxKey = null;
            boolean isInfoboxQuery = false;
            int numberOfSearchTerms = 0;

            String[] searchTokens = searchQuery.split(" ");

            for ( String s : searchTokens ) {

                s = s.toLowerCase();
                StringBuilder stringBuilder = new StringBuilder();
                for ( int j = 0 ; j < s.length() ; j++ ) {
                    char currentChar = s.charAt(j);
                    if ( currentChar == ':' || ((int)currentChar >= (int)'a' && (int)currentChar <= (int)'z') )
                        stringBuilder.append(currentChar);
                }

                s = new String(stringBuilder);
                if ( s.length() == 0 )
                    continue;

                int index = s.indexOf(':');
                if ( index == -1 ) {
                    // non field query

                    if ( Classifiers.isStopword(s) )
                        continue;

                    Set<String> list = searchTermMap.get((int)s.charAt(0) - (int)'a');
                    list.add(s);

                    numberOfSearchTerms ++;

                }
                else {

                    if ( index == -1 ) {
                        char c = s.charAt(0);
                        index = (int)c - (int)'a';

                        Set<String> list = searchTermMap.get(index);
                        list.add(s);
                        continue;
                    }
                    String type = s.substring(0,index).toLowerCase();
                    String value = s.substring(index+1);

                    if ( value.length() == 0 || Classifiers.isStopword(value) )
                        continue;

                    numberOfSearchTerms++;

                    char c = value.charAt(0);

                    Set<String> list = searchTermMap.get(index);
                    list.add(value);

                    if ( type.equals("t") ) {

                        Integer val = searchFieldsForTerms.get(value);
                        if ( val == null )
                            val = 0;
                        val = val | TITLE;
                        searchFieldsForTerms.put(value , val);

                    }
                    else if (type.equals("b")) {

                        Integer val = searchFieldsForTerms.get(value);
                        if ( val == null )
                            val = 0;
                        val = val | BODY;
                        searchFieldsForTerms.put(value , val);
                    }
                    else if (type.equals("l")) {

                        Integer val = searchFieldsForTerms.get(value);
                        if ( val == null )
                            val = 0;
                        val = val | LINKS;
                        searchFieldsForTerms.put(value , val);
                    }
                    else if (type.equals("i")) {

                        Integer val = searchFieldsForTerms.get(value);
                        if ( val == null )
                            val = 0;
                        val = val | BODY;
                        searchFieldsForTerms.put(value , val);
                    }
                    else if (type.equals("c")) {

                        Integer val = searchFieldsForTerms.get(value);
                        if ( val == null )
                            val = 0;
                        val = val | CATEGORY;
                        searchFieldsForTerms.put(value , val);
                    }
                    else {

                        infoboxKey = type;
                        Set<String> list1 = searchTermMap.get((int) value.charAt(0) - (int) 'a');
                        if ( list1 == null ) {
                            list1 = new HashSet<String>();
                        }
                        list1.add(value);
                        isInfoboxQuery = true;

                    }
                }
            }

            ArrayList<String> listDocIds = wikiSearch.search(numberOfSearchTerms , searchTermMap , searchFieldsForTerms);

            if ( listDocIds == null )
                listDocIds = new ArrayList<String>();

            long stopTime = System.currentTimeMillis();
            float elapsedTime = (stopTime - startTime)/1000f;

            String maxDocId = null;
            long maxOffset = 0;

            ArrayList<StringBuilder> listOfTitles = new ArrayList<StringBuilder>();

            int countOf0Title = 0;

            for ( String docId : listDocIds ) {

                try {

                    String temp = wikiSearch.getWikiPageTitle(docId);
                    int indexOf = temp.indexOf(':');
                    String offset = temp.substring(0,indexOf);
                    String title = temp.substring(indexOf + 1);

                    StringBuilder titlePlusId = new StringBuilder(docId);
                    titlePlusId.append('-');
                    titlePlusId.append(title);


                    //System.out.println("Page : " + docId + " title : " + title);

                    if ( offset.equals("0") || title.indexOf(':') >= 0 ) {
                        //listOfTitles.add(0,title);
                        listOfTitles.add(0,titlePlusId);
                        countOf0Title ++;
                    }
                    else {
                        //listOfTitles.add(title);
                        listOfTitles.add(titlePlusId);
                        if ( maxDocId == null ) {
                            maxDocId = docId;
                            maxOffset = Long.parseLong(offset);
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }


            for ( int j = 0 ; j < countOf0Title ; j++ ) {
                StringBuilder s = listOfTitles.remove(0);
                listOfTitles.add(listOfTitles.size() , s);
            }

            int limit = 10;
            if ( listOfTitles.size() < limit ) {
                limit = listOfTitles.size();
            }

            for ( int j = 0 ; j < limit ; j++ ) {
                System.out.println(listOfTitles.get(j));
            }


            if ( limit == 0 ) {
                System.out.println("No results found !");
            }

            if ( isInfoboxQuery && maxOffset != 0 && infoboxKey.length() > 0 ) {
                // go to info.dat for docID and offset maxOffset.

                RandomAccessFile randomAccessFile = null;
                try {
                    randomAccessFile = new RandomAccessFile(indexFolderPath + "meta/info.dat" , "r");
                    randomAccessFile.seek(maxOffset);
                    String line = randomAccessFile.readLine();

                    HashMap<String,String> infoboxMap = new HashMap<String, String>();

                    while ( line != null && !line.equals(":") ) {
                        //System.out.println(line);
                        int indexOf = line.indexOf(':');
                        if ( indexOf == -1 ) {
                            line = randomAccessFile.readLine();
                            continue;
                        }

                        String key = line.substring(0,indexOf);
                        String value = line.substring(indexOf+1);
                        infoboxMap.put(key,value);
                        line = randomAccessFile.readLine();
                    }

                    int minEditDistance = 100;
                    String minKey = null;

                    System.out.println("***********************");
                    System.out.println("Results for page : " + listOfTitles.get(0));
                    //System.out.println("INfobox Key : " + infoboxKey);
                    for ( String key : infoboxMap.keySet() ) {

                        if ( key.length() > 20 )
                            continue;
                        String value = infoboxMap.get(key);
                        if ( key.contains(infoboxKey) || value.contains(infoboxKey) ) {
                            /*int currentDistance = editDistance(key , infoboxKey);
                            if ( currentDistance < minEditDistance ) {
                                minEditDistance = currentDistance;
                                minKey = key;
                            }*/

                            System.out.print(key + " ");
                            System.out.println(value);
                        }

                    }
                    System.out.println("***********************");



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

            }

            System.out.println(elapsedTime + "sec.");

        }
    }

}