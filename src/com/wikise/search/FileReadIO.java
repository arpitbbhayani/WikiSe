package com.wikise.search;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 14/1/14.
 */
public class FileReadIO {

    boolean isInitialized = false;

    public String[] fileNames = {
            "indexa.idx" ,"indexb.idx" ,"indexc.idx" ,"indexd.idx" ,"indexe.idx" ,"indexf.idx" ,"indexg.idx" ,
            "indexh.idx" ,"indexi.idx" ,"indexj.idx" ,"indexk.idx" ,"indexl.idx" ,"indexm.idx" ,"indexn.idx" ,
            "indexo.idx" ,"indexp.idx" ,"indexq.idx" ,"indexr.idx" ,"indexs.idx" ,"indext.idx" ,"indexu.idx" ,
            "indexv.idx" ,"indexw.idx" ,"indexx.idx" ,"indexy.idx" ,"indexz.idx"
    };

    private String indexFolderPath = null;

    public FileReadIO(String folderPath) {

        if ( folderPath.charAt(folderPath.length()-1) != '/' ) {
            this.indexFolderPath = folderPath + '/';
        }
        else {
            this.indexFolderPath = folderPath;
        }
    }

    public TreeSet<Integer> readData(char startChar , TreeSet<Integer> listOfSeeks) {

        if ( listOfSeeks == null )
            return new TreeSet<Integer>();

        TreeSet<Integer> pageIds = new TreeSet<Integer>();
        int index = ((int)startChar) - ((int)'a');

        RandomAccessFile randomAccessFile = null;

        try {

            randomAccessFile = new RandomAccessFile(indexFolderPath + fileNames[index] , "r");

            StringBuffer stringBuffer = new StringBuffer();

            for ( int seekLocation : listOfSeeks ) {

                randomAccessFile.seek(seekLocation);

                String line = randomAccessFile.readLine();
                String[] splitted = line.split(":");

                for ( String split : splitted ) {
                    if ( Character.isDigit(split.charAt(0)) )
                        pageIds.add(new Integer(split));
                }

                /*char ch = file[index].readChar();
                while ( ch != ':') {
                    ch = file[index].readChar();
                }

                ch = file[index].readChar();

                while  ( (int) ch != -1 && (Character.isDigit(ch) || ch == ':') ) {

                    if ( ch == ':' ) {
                        pageIds.add(new Integer(new String(stringBuffer)));
                        stringBuffer.setLength(0);
                    }
                    else {
                        stringBuffer.append(ch);
                    }
                    ch = (char) file[index].read();
                }

                if ( stringBuffer.length() > 0 ) {
                    pageIds.add(new Integer(new String(stringBuffer)));
                    stringBuffer.setLength(0);
                }*/

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pageIds;
    }

}
