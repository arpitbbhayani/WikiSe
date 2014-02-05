package com.wikise.util;

import com.wikise.parse.WikiPageConcise;

import java.io.*;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 14/1/14.
 */
public class HashedFileIO {

    int fileSeeks[] = new int[26];

    public String metaFileName = "metadata.dat";

    public String[] fileNames = {
            "indexa.idx" ,"indexb.idx" ,"indexc.idx" ,"indexd.idx" ,"indexe.idx" ,"indexf.idx" ,"indexg.idx" ,
            "indexh.idx" ,"indexi.idx" ,"indexj.idx" ,"indexk.idx" ,"indexl.idx" ,"indexm.idx" ,"indexn.idx" ,
            "indexo.idx" ,"indexp.idx" ,"indexq.idx" ,"indexr.idx" ,"indexs.idx" ,"indext.idx" ,"indexu.idx" ,
            "indexv.idx" ,"indexw.idx" ,"indexx.idx" ,"indexy.idx" ,"indexz.idx"
    };

    public String[] sfileNames = {
            "sindexa.idx" ,"sindexb.idx" ,"sindexc.idx" ,"sindexd.idx" ,"sindexe.idx" ,"sindexf.idx" ,"sindexg.idx" ,
            "sindexh.idx" ,"sindexi.idx" ,"sindexj.idx" ,"sindexk.idx" ,"sindexl.idx" ,"sindexm.idx" ,"sindexn.idx" ,
            "sindexo.idx" ,"sindexp.idx" ,"sindexq.idx" ,"sindexr.idx" ,"sindexs.idx" ,"sindext.idx" ,"sindexu.idx" ,
            "sindexv.idx" ,"sindexw.idx" ,"sindexx.idx" ,"sindexy.idx" ,"sindexz.idx"
    };

    String indexFolderPath = null;

    private BufferedWriter[] writer = new BufferedWriter[fileNames.length];
    private BufferedWriter[] swriter = new BufferedWriter[sfileNames.length];

    private BufferedWriter metaWriter;

    public HashedFileIO(String folderPath) {

        if ( folderPath.charAt(folderPath.length()-1) != '/' ) {
            this.indexFolderPath = folderPath + '/';
        }
        else {
            this.indexFolderPath = folderPath;
        }
    }

    public void initialize() {
        try {
            metaWriter = new BufferedWriter(new FileWriter(indexFolderPath + metaFileName));
            for ( int i = 0 ; i < fileNames.length ; i++ ) {
                writer[i] = new BufferedWriter(new FileWriter(indexFolderPath + fileNames[i]));
                fileSeeks[i] = 0;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            metaWriter.close();
            for ( int i = 0 ; i < fileNames.length ; i++ ) {
                writer[i].close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int writeData(StringBuilder wordToWrite) {

        wordToWrite.append('\n');

        String wordString = new String(wordToWrite);
        char startChar = wordToWrite.charAt(0);
        int index = ((int)startChar) - ((int)'a');
        int seekLocation = -1;

        if ( index < 26) {
            seekLocation = fileSeeks[index];
            try {
                writer[index].write(wordString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileSeeks[index] += wordString.getBytes().length;

        }

        return seekLocation;
    }

    public void sInitialize() {
        try {
            for ( int i = 0 ; i < sfileNames.length ; i++ ) {
                swriter[i] = new BufferedWriter(new FileWriter(indexFolderPath + sfileNames[i]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sClose() {
        try {
            for ( int i = 0 ; i < sfileNames.length ; i++ ) {
                swriter[i].close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dumpSecondary(TreeSet<String> allStrings, HashedTrie trie) {

        StringBuilder stringBuilder = new StringBuilder();

        for ( String word : allStrings ) {
            TreeSet<Integer> seekLocations = trie.contains(word);
            if ( seekLocations != null ) {

                stringBuilder.setLength(0);

                stringBuilder.append(word);
                for ( Integer seekLoc : seekLocations ) {
                    stringBuilder.append(":");
                    stringBuilder.append(seekLoc);
                }

                stringBuilder.append('\n');

                char startChar = word.charAt(0);
                int index = ((int)startChar) - ((int)'a');

                if ( index < 26) {

                    try {
                        swriter[index].write(new String(stringBuilder));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    stringBuilder.setLength(0);

                }

            }
        }

    }

    public void dumpMetaInformation(WikiPageConcise wikiPage) {

        try {
            metaWriter.write(wikiPage.getPageId() + ":" + wikiPage.getPageTitile() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
