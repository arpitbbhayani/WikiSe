package com.wikise.search;

import com.wikise.parse.WikiParse;
import com.wikise.search.Classifiers;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Search {
    public static void main(String[] args) {

        //String indexFolderPath = "/home/devilo/workspace/java/201305515_M1/bin/index";
        String indexFolderPath = args[0];

        FileReadIO fileReadIO = new FileReadIO(indexFolderPath);
        FileSecondaryReadIO fileSecondaryReadIO = new FileSecondaryReadIO(indexFolderPath);
        //FileSequentialReadIO fileSequentialReadIO = new FileSequentialReadIO();
        Classifiers.initialize();

        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        scanner.nextLine();

        for ( int i = 0 ; i < count ; i++ ) {

            String searchQuery = scanner.nextLine();
            //long startTime = System.currentTimeMillis();
                TreeSet<Integer> listOfDocId = search(searchQuery.toLowerCase() , fileReadIO , fileSecondaryReadIO);
                formatOutput(listOfDocId);
            //long stopTime = System.currentTimeMillis();
            //System.out.println( (stopTime - startTime) / 1000f );
        }

    }

    private static void formatOutput(TreeSet<Integer> listOfDocId) {

        int length = listOfDocId.size();
        if ( length >= 1 ) {
            Integer docId = listOfDocId.pollFirst();
            System.out.print(docId);
            for ( int i = 1 ; i < length ; i++ ) {
                System.out.print("," + listOfDocId.pollFirst());
            }
        }
        System.out.println("");

    }


    private static TreeSet<Integer> ssearch(String searchQuery, FileSequentialReadIO fileSequentialReadIO) {
        String searchWord = Classifiers.getStemmedWord(searchQuery.split("[^a-zA-Z]")[0].toLowerCase());
        return fileSequentialReadIO.readData(searchWord);
    }

    private static TreeSet<Integer> search(String searchQuery, FileReadIO fileReadIO, FileSecondaryReadIO fileSecondaryReadIO) {


        StringBuilder stringBuilder = new StringBuilder();
        for ( int i = 0 ; i < searchQuery.length() ; i++ ) {
            char currentChar = Character.toLowerCase(searchQuery.charAt(i));
            if ( (int) (currentChar) >= (int) 'a' &&  (int) (currentChar) <= (int) 'z'  )
                stringBuilder.append(currentChar);
        }

        String toBeSearched = new String(stringBuilder);
        if ( toBeSearched.length() == 0 || Classifiers.isStopword(toBeSearched)) {
            return new TreeSet<Integer>();
        }

        String searchWord = Classifiers.getStemmedWord(Classifiers.getStemmedWord(toBeSearched));
        TreeSet<Integer> seekLocations = fileSecondaryReadIO.getSeekLocations(searchWord);

        return fileReadIO.readData(searchWord.charAt(0) , seekLocations);

    }
}