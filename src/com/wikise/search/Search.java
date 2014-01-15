package com.wikise.search;

import com.wikise.search.Classifiers;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Search {
    public static void main(String[] args) {

        FileReadIO fileReadIO = new FileReadIO();
        FileSecondaryReadIO fileSecondaryReadIO = new FileSecondaryReadIO();
        FileSequentialReadIO fileSequentialReadIO = new FileSequentialReadIO();
        Classifiers.initialize();

        Scanner scanner = new Scanner(System.in);
        int count = 200;

        while ( count != 0 ) {
            String searchQuery = scanner.nextLine();
            long startTime = System.currentTimeMillis();
                System.out.println("Secondary : " + search(searchQuery.toLowerCase() , fileReadIO , fileSecondaryReadIO));
            long stopTime = System.currentTimeMillis();
            System.out.println( (stopTime - startTime) / 1000f );
            /*startTime = System.currentTimeMillis();
                System.out.println("Sequential : " + ssearch(searchQuery.toLowerCase(), fileSequentialReadIO));
            stopTime = System.currentTimeMillis();
            System.out.println( (stopTime - startTime) / 1000f );*/
            count --;
        }

        fileReadIO.close();
        fileSecondaryReadIO.close();
        fileSequentialReadIO.close();

    }

    private static TreeSet<Integer> ssearch(String searchQuery, FileSequentialReadIO fileSequentialReadIO) {
        String searchWord = Classifiers.getStemmedWord(searchQuery.split("[^a-zA-Z]")[0].toLowerCase());
        return fileSequentialReadIO.readData(searchWord);
    }

    private static TreeSet<Integer> search(String searchQuery, FileReadIO fileReadIO, FileSecondaryReadIO fileSecondaryReadIO) {

        String searchWord = Classifiers.getStemmedWord(searchQuery.split("[^a-zA-Z]")[0].toLowerCase());
        TreeSet<Integer> seekLocations = fileSecondaryReadIO.getSeekLocations(searchWord);

        return fileReadIO.readData(searchWord.charAt(0) , seekLocations);

    }
}