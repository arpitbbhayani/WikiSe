package com.wikise.main;

import com.wikise.parse.WikiParse;

import java.util.Scanner;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Main {
    public static void main(String[] args) {

        /*long startTime = System.currentTimeMillis();
            WikiParse wikiParse = new WikiParse("/home/devilo/IRE/SearchEngine/sample1.xml");
            wikiParse.parse();
        long stopTime = System.currentTimeMillis();
        System.out.println( (stopTime - startTime) / 1000f );*/

        String filePath = args[0];
        String indexFolderPath = args[1];

        WikiParse wikiParse = new WikiParse(filePath);
        wikiParse.parse(indexFolderPath);

    }
}