package com.wikise.search;

import com.wikise.process.Stemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 14/1/14.
 */
public class FileSequentialReadIO {

    boolean isInitialized = false;

    public String[] fileNames = {
            "indexa.idx" ,"indexb.idx" ,"indexc.idx" ,"indexd.idx" ,"indexe.idx" ,"indexf.idx" ,"indexg.idx" ,
            "indexh.idx" ,"indexi.idx" ,"indexj.idx" ,"indexk.idx" ,"indexl.idx" ,"indexm.idx" ,"indexn.idx" ,
            "indexo.idx" ,"indexp.idx" ,"indexq.idx" ,"indexr.idx" ,"indexs.idx" ,"indext.idx" ,"indexu.idx" ,
            "indexv.idx" ,"indexw.idx" ,"indexx.idx" ,"indexy.idx" ,"indexz.idx"
    };

    public TreeSet<Integer> readData(String searchWord ) {

        TreeSet<Integer> pageIds = new TreeSet<Integer>();
        int index = (int)(searchWord.charAt(0)) - ((int)'a');

        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(fileNames[index]));

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
        finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pageIds;
    }

}
