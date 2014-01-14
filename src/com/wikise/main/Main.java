package com.wikise.main;

import com.wikise.parse.WikiParse;

import java.util.Scanner;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Main {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
            WikiParse wikiParse = new WikiParse("/home/devilo/IRE/SearchEngine/sample.xml");
            wikiParse.parse();
        long stopTime = System.currentTimeMillis();
        System.out.println( (stopTime - startTime) / 1000f );

        System.out.println("Index Generated");

        Scanner scanner = new Scanner(System.in);

        while ( true ) {
            System.out.print("Search for : ");
            String searchQuery = scanner.nextLine();
            startTime = System.currentTimeMillis();
                System.out.println(wikiParse.search(searchQuery));
            stopTime = System.currentTimeMillis();
            System.out.println( (stopTime - startTime) / 1000f );
        }

    }
}