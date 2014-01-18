package com.wikise.main;

import com.wikise.parse.WikiParse;

import java.util.Scanner;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Main {
    public static void main(String[] args) {

        String filePath = "/home/devilo/Downloads/100.xml";
        String indexFolderPath = "/home/devilo/workspace/java/index";

        //String filePath = args[0];
        //String indexFolderPath = args[1];

        WikiParse wikiParse = new WikiParse(filePath);
        wikiParse.parse(indexFolderPath);

    }
}