package com.wikise.process;

import com.wikise.parse.WikiPageConcise;
import com.wikise.util.Classifiers;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 13/1/14.
 */
public class WikiTextParser {

    String wikiText = null;
    String wikiPageId = null;
    String wikiPageTitle = null;

    TreeSet<String> setWords = null;

    public WikiTextParser(WikiPageConcise wikiPage, TreeMap<String, TreeSet<String>> invertedIndex) {

        this.wikiText = wikiPage.getPageText();
        this.wikiPageId = wikiPage.getPageId();
        this.wikiPageTitle = wikiPage.getPageTitile();

        this.setWords = new TreeSet<String>();

    }

    public String getText() {
        return this.wikiText;
    }

    public TreeSet<String> parseText() {

        int i = 0;
        int wikiTextLength = this.wikiText.length();

        StringBuilder stringBuilder = new StringBuilder();

        for ( i = 0 ; i < wikiTextLength ; i++ ) {

            char currentChar = wikiText.charAt(i);

            if ( ((int)currentChar) < 128 && Character.isLetter(currentChar) ) {
                stringBuilder.append(currentChar);
            }
            else if ( currentChar == '{' ) {

                if ( i+9 < wikiTextLength && wikiText.substring(i+1,i+9).equals("{infobox") ) {

                    StringBuilder infoboxString = new StringBuilder();

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);
                        infoboxString.append(currentChar);
                        if ( currentChar == '{') {
                            count++;
                        }
                        else if ( currentChar == '}') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            if ( currentChar == '=' ) {infoboxString.deleteCharAt(infoboxString.length()-1);}
                            break;
                        }
                    }

                    processInfobox(infoboxString);

                }
                else if ( i+8 < wikiTextLength && wikiText.substring(i+1,i+8).equals("{geobox") ) {

                    StringBuilder geoboxString = new StringBuilder();

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);
                        geoboxString.append(currentChar);
                        if ( currentChar == '{') {
                            count++;
                        }
                        else if ( currentChar == '}') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            if ( currentChar == '=' ) {geoboxString.deleteCharAt(geoboxString.length()-1);}
                            break;
                        }
                    }

                    processGeobox(geoboxString);

                }
                else if ( i+6 < wikiTextLength && wikiText.substring(i+1,i+6).equals("{cite") ) {

                    /*
                     *  Citations are to be removed.
                     */

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);
                        if ( currentChar == '{') {
                            count++;
                        }
                        else if ( currentChar == '}') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            break;
                        }
                    }

                }
                else if ( i+4 < wikiTextLength && wikiText.substring(i+1,i+4).equals("{gr") ) {

                    /*
                     *  {{GR .. to be removed
                     */

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);
                        if ( currentChar == '{') {
                            count++;
                        }
                        else if ( currentChar == '}') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            break;
                        }
                    }

                }
                else if ( i+7 < wikiTextLength && wikiText.substring(i+1,i+7).equals("{coord") ) {

                    /**
                     * Coords to be removed
                     */

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);

                        if ( currentChar == '{') {
                            count++;
                        }
                        else if ( currentChar == '}') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            break;
                        }
                    }

                }

            }
            else if ( currentChar == '[' ) {

                if ( i+11 < wikiTextLength && wikiText.substring(i+1,i+11).equals("[category:") ) {

                    StringBuilder categoryString = new StringBuilder();

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);
                        categoryString.append(currentChar);
                        if ( currentChar == '[') {
                            count++;
                        }
                        else if ( currentChar == ']') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            if ( currentChar == '=' ) {categoryString.deleteCharAt(categoryString.length()-1);}
                            break;
                        }
                    }

                    processCategories(categoryString);

                }
                else if ( i+8 < wikiTextLength && wikiText.substring(i+1,i+8).equals("[image:") ) {

                    /**
                     * Images to be removed
                     */

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);
                        if ( currentChar == '[') {
                            count++;
                        }
                        else if ( currentChar == ']') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            break;
                        }
                    }

                }
                else if ( i+7 < wikiTextLength && wikiText.substring(i+1,i+7).equals("[file:") ) {

                    /**
                     * File to be removed
                     */

                    int count = 0;
                    for ( ; i < wikiTextLength ; i++ ) {

                        currentChar = wikiText.charAt(i);

                        if ( currentChar == '[') {
                            count++;
                        }
                        else if ( currentChar == ']') {
                            count--;
                        }
                        if ( count == 0 || (currentChar == '=' && i+1 < wikiTextLength && wikiText.charAt(i+1) == '=')) {
                            break;
                        }
                    }

                }
            }
            else if ( currentChar == '<') {

                if ( i+4 < wikiTextLength && wikiText.substring(i+1,i+4).equals("!--") ) {

                    /**
                     * Comments to be removed
                     */

                    int locationClose = wikiText.indexOf("-->" , i+1);
                    if ( locationClose == -1 || locationClose+2 > wikiTextLength ) {
                        i = wikiTextLength-1;
                    }
                    else {
                        i = locationClose+2;
                    }

                }
                else if ( i+4 < wikiTextLength && wikiText.substring(i+1,i+4).equals("ref") ) {

                    /**
                     * References to be removed
                     */
                    int locationClose = wikiText.indexOf("</ref>" , i+1);
                    if ( locationClose == -1 || locationClose+5 > wikiTextLength ) {
                        i = wikiTextLength-1;
                    }
                    else {
                        i = locationClose+5;
                    }

                }
                else if ( i+8 < wikiTextLength && wikiText.substring(i+1,i+8).equals("gallery") ) {

                    /**
                     * Gallery to be removed
                     */
                    int locationClose = wikiText.indexOf("</gallery>" , i+1);
                    if ( locationClose == -1 || locationClose+9 > wikiTextLength) {
                        i = wikiTextLength-1;
                    }
                    else {
                        i = locationClose+9;
                    }
                }
            }
            else {
                String word = new String(stringBuilder);
                processWord(word);
                stringBuilder.setLength(0);
            }
        }

        if ( stringBuilder.length() != 0 ) {
            String word = new String(stringBuilder);
            processWord(word);
            stringBuilder.setLength(0);
        }

        processTitle(wikiPageTitle);

        return setWords;
    }

    private void processWord(String word) {
        if ( word.length() > 1 && !Classifiers.isStopword(word)) {

            setWords.add(Classifiers.getStemmedWord(word));

        }
    }

    private void processGeobox(StringBuilder geoboxString) {
        int length = geoboxString.length();
        StringBuilder stringBuilder = new StringBuilder();

        for ( int i = 0 ; i < length ; i++ ) {
            char currentChar = geoboxString.charAt(i);
            if ( Character.isLetter(currentChar) )
                stringBuilder.append(currentChar);
            else {
                String word = new String(stringBuilder);
                processWord(word);
                stringBuilder.setLength(0);
            }
        }
        if ( stringBuilder.length() > 0 ) {
            String word = new String(stringBuilder);
            processWord(word);
            stringBuilder.setLength(0);
        }
    }

    private void processCategories(StringBuilder categoryString) {
        int length = categoryString.length();
        StringBuilder stringBuilder = new StringBuilder();

        for ( int i = 0 ; i < length ; i++ ) {
            char currentChar = categoryString.charAt(i);
            if ( Character.isLetter(currentChar) )
                stringBuilder.append(currentChar);
            else {
                String word = new String(stringBuilder);
                processWord(word);
                stringBuilder.setLength(0);
            }
        }
        if ( stringBuilder.length() > 0 ) {
            String word = new String(stringBuilder);
            processWord(word);
            stringBuilder.setLength(0);
        }
    }

    private void processInfobox(StringBuilder infoboxString) {

        int length = infoboxString.length();
        StringBuilder stringBuilder = new StringBuilder();

        for ( int i = 0 ; i < length ; i++ ) {
            char currentChar = infoboxString.charAt(i);
            if ( Character.isLetter(currentChar) )
                stringBuilder.append(currentChar);
            else {
                String word = new String(stringBuilder);
                processWord(word);
                stringBuilder.setLength(0);
            }
        }
        if ( stringBuilder.length() > 0 ) {
            String word = new String(stringBuilder);
            processWord(word);
            stringBuilder.setLength(0);
        }

    }

    private void processTitle(String titleString) {

        int length = titleString.length();
        StringBuilder stringBuilder = new StringBuilder();

        for ( int i = 0 ; i < length ; i++ ) {
            char currentChar = titleString.charAt(i);
            if ( Character.isLetter(currentChar) )
                stringBuilder.append(currentChar);
            else {
                String word = new String(stringBuilder);
                processWord(word);
                stringBuilder.setLength(0);
            }
        }
        if ( stringBuilder.length() > 0 ) {
            String word = new String(stringBuilder);
            processWord(word);
            stringBuilder.setLength(0);
        }

    }

}
