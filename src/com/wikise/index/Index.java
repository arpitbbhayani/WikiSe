package com.wikise.index;

import com.wikise.parse.WikiParse;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Index {
    public static void main(String[] args) {

        String filePath = "/media/devilo/GaMeS aNd SeTuPs/100.xml";
        //String filePath = "/media/devilo/GaMeS aNd SeTuPs/enwiki-latest-pages-articles.xml/enwiki-latest-pages-articles.xml";
        String indexFolderPath = "/media/devilo/GaMeS aNd SeTuPs/datas";

        //String filePath = args[0];
        //String indexFolderPath = args[1];

        long startTime = System.currentTimeMillis();

        WikiParse wikiParse = new WikiParse(filePath);
        wikiParse.parse(indexFolderPath);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);

    }
}