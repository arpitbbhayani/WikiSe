package com.wikise.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Arpit Bhayani on 8/2/14.
 */
public class SearchThread extends Thread {

    ConcurrentHashMap<String , Double> documentToTfIdf = null;
    ArrayList<HashSet<String>> s = null;
    HashMap<String, Integer> searchFieldsForTerms = null;
    int index = 0;
    FileReadIO fileReadIO = null;

    public SearchThread(FileReadIO fileReadIO , int index , ConcurrentHashMap<String , Double> map , ArrayList<HashSet<String>> s, HashMap<String, Integer> searchFieldsForTerms) {
        this.index = index;
        this.documentToTfIdf = map;
        this.s = s;
        this.searchFieldsForTerms = searchFieldsForTerms;
        this.fileReadIO = fileReadIO;
    }
    public void run() {

        try {
        HashSet<String> setOfWords = s.get(index);
        for ( String searchTerm : setOfWords ) {

            Integer requiredFields = searchFieldsForTerms.get(searchTerm);

            if ( requiredFields == null )
                requiredFields = 40;

            ArrayList<String> tempList = fileReadIO.getPostingsForSingleTerm(searchTerm);
            for ( String entity : tempList ) {

                int length = entity.length();
                StringBuilder docId = new StringBuilder();
                int bitRepresentation = 0;
                int termFrequency = 0;

                int j = 0;
                char currentChar;
                for ( ; (currentChar = entity.charAt(j)) != '$' ; j++ ) {
                    docId.append(currentChar);
                }
                for ( j++ ; (currentChar = entity.charAt(j)) != '$' ; j++ ) {
                    bitRepresentation  = bitRepresentation * 10 + ((int)currentChar - (int)'0');
                }
                for ( j++ ; j < length && (currentChar = entity.charAt(j)) != '$' ; j++ ) {
                    termFrequency  = termFrequency * 10 + ((int)currentChar - (int)'0');
                }

                String docIdStr = new String(docId);
                if ( ((requiredFields & 32) != 0 && (bitRepresentation & 32) != 0) ||
                        ((requiredFields & 16) != 0 && (bitRepresentation & 16) != 0) ||
                        ((requiredFields & 8) != 0 && (bitRepresentation & 8) != 0) ||
                        ((requiredFields & 4) != 0 && (bitRepresentation & 4) != 0)) {

                    Double oldTfidf = documentToTfIdf.get(docId);
                    if ( oldTfidf == null )
                        oldTfidf = Double.valueOf(0);
                    Double newTfIdf = tfidf(termFrequency , tempList.size() );

                    //median.addNumberToStream(newTfIdf);

                    if ( newTfIdf > oldTfidf )
                        documentToTfIdf.put(docIdStr , newTfIdf );

                }
            }
        }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private double tfidf(int tf, int df) {

        return (1+Math.log10(tf)) * Math.log10(14128976/(float)df);

    }

}
