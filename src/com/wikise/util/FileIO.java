package com.wikise.util;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 14/1/14.
 */
public class FileIO {

    int fileSeeks[] = new int[26];

    public String[] fileNames = {
            "indexa.idx" ,"indexb.idx" ,"indexc.idx" ,"indexd.idx" ,"indexe.idx" ,"indexf.idx" ,"indexg.idx" ,
            "indexh.idx" ,"indexi.idx" ,"indexj.idx" ,"indexk.idx" ,"indexl.idx" ,"indexm.idx" ,"indexn.idx" ,
            "indexo.idx" ,"indexp.idx" ,"indexq.idx" ,"indexr.idx" ,"indexs.idx" ,"indext.idx" ,"indexu.idx" ,
            "indexv.idx" ,"indexw.idx" ,"indexx.idx" ,"indexy.idx" ,"indexz.idx"
    };

    private BufferedWriter[] writer = new BufferedWriter[fileNames.length];

    public void initialize() {
        try {
            for ( int i = 0 ; i < fileNames.length ; i++ ) {
                writer[i] = new BufferedWriter(new FileWriter(fileNames[i]));
                fileSeeks[i] = 0;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            for ( int i = 0 ; i < fileNames.length ; i++ ) {
                writer[i].close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int writeData(StringBuilder wordToWrite) {

        char startChar = wordToWrite.charAt(0);
        int index = ((int)startChar) - ((int)'a');
        int seekLocation = -1;

        if ( index < 26) {
            seekLocation = fileSeeks[index];
            try {
                writer[index].write(String.valueOf(wordToWrite), 0, wordToWrite.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileSeeks[index] += wordToWrite.length();
        }

        return seekLocation+1;
    }

}
