package com.wikise.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 14/1/14.
 */
public class FileSecondaryReadIO {

    boolean isInitialized = false;

    public String[] fileNames = {
            "sindexa.idx" ,"sindexb.idx" ,"sindexc.idx" ,"sindexd.idx" ,"sindexe.idx" ,"sindexf.idx" ,"sindexg.idx" ,
            "sindexh.idx" ,"sindexi.idx" ,"sindexj.idx" ,"sindexk.idx" ,"sindexl.idx" ,"sindexm.idx" ,"sindexn.idx" ,
            "sindexo.idx" ,"sindexp.idx" ,"sindexq.idx" ,"sindexr.idx" ,"sindexs.idx" ,"sindext.idx" ,"sindexu.idx" ,
            "sindexv.idx" ,"sindexw.idx" ,"sindexx.idx" ,"sindexy.idx" ,"sindexz.idx"
    };

    BufferedReader[] file = new BufferedReader[fileNames.length];

    public void initialize() {

        try {
            for ( int i = 0 ; i < fileNames.length ; i++ ) {
                file[i] = new BufferedReader(new FileReader(fileNames[i]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            for ( int i = 0 ; i < fileNames.length ; i++ ) {
                file[i].close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeSet<Integer> getSeekLocations(String searchWord ) {

        if ( isInitialized == false ) {
            initialize();
            isInitialized = true;
        }

        TreeSet<Integer> pageIds = new TreeSet<Integer>();
        int index = (int)(searchWord.charAt(0)) - ((int)'a');

        BufferedReader bufferedReader = file[index];

        try {
            String currentLine = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = bufferedReader.readLine()) != null) {

                int indexOfColon = currentLine.indexOf(':');
                String word = currentLine.substring(0,indexOfColon);
                if ( word.equals(searchWord)) {

                    char currentChar = 0;
                    int length = currentLine.length();

                    for ( int i = indexOfColon+1 ; i < length ; i++ ) {
                        currentChar = currentLine.charAt(i);
                        if ( currentChar == ':' ) {
                            pageIds.add(new Integer(new String(stringBuilder)));
                            stringBuilder.setLength(0);
                        }
                        else {
                            stringBuilder.append(currentChar);
                        }
                    }

                    if ( stringBuilder.length() > 0 ) {
                        pageIds.add(new Integer(new String(stringBuilder)));
                        stringBuilder.setLength(0);
                    }

                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return pageIds;
    }

}
