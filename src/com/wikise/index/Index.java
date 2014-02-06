package com.wikise.index;

import com.wikise.parse.WikiParse;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class Index {
    public static void main(String[] args) {

        String filePath = "/media/devilo/GaMeS aNd SeTuPs/100.xml";
        String indexFolderPath = "/media/devilo/GaMeS aNd SeTuPs/datas";

        long startTime = System.currentTimeMillis();

        WikiParse wikiParse = new WikiParse(filePath);
        wikiParse.parse(indexFolderPath);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);

    }
}