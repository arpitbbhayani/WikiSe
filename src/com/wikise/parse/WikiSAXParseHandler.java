package com.wikise.parse;

import com.wikise.process.WikiTextParser;
import com.wikise.util.FileIO;
import com.wikise.util.Trie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class WikiSAXParseHandler extends DefaultHandler {

    int numberOfPages = 0;
    int totalWords = 0;

    Stack<String> tagStack = null;
    StringBuilder stringBuilder = null;
    TreeMap<String , TreeSet<String>> invertedIndex = null;

    Trie trie = null;
    String indexFolderPath = null;

    TreeSet<String> allStrings = new TreeSet<String>();

    WikiPageConcise wikiPage = null;
    String wikiPageId = null;

    FileIO fileIO = null;

    public WikiSAXParseHandler(Trie trie, String indexFolderPath) {
        tagStack = new Stack<String>();
        stringBuilder = new StringBuilder();
        invertedIndex = new TreeMap<String, TreeSet<String>>();

        this.trie = trie;
        this.indexFolderPath = indexFolderPath;

        fileIO = new FileIO(indexFolderPath);
        fileIO.initialize();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if ( qName.equalsIgnoreCase("page") )
            wikiPage = new WikiPageConcise();

        stringBuilder.setLength(0);
        tagStack.push(qName);

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        String endTag = tagStack.pop();
        String parentTag = null;

        if ( !tagStack.isEmpty() )
            parentTag = tagStack.peek();

        if ( endTag.equalsIgnoreCase("page") ) {

            WikiTextParser wikiTextParser = new WikiTextParser(wikiPage , invertedIndex );
            //pageMap.put(wikiPage.getPageId() , wikiTextParser.parseText());
            //allStrings.addAll(wikiTextParser.parseText());

            TreeSet<String> tempSet = wikiTextParser.parseText();

            for ( String word : tempSet ) {

                TreeSet<String> docList = invertedIndex.get(word);
                if ( docList == null ) {
                    docList = new TreeSet<String>();
                    docList.add(wikiPageId);
                    invertedIndex.put(word , docList);
                }
                else {
                    docList.add(wikiPageId);
                }

            }

            numberOfPages ++;

            if ( numberOfPages == 1000 ) {

                allStrings.addAll(invertedIndex.keySet());


                for ( String wordInvertedIndex : invertedIndex.keySet() ) {
                    StringBuilder wordToWrite = new StringBuilder(wordInvertedIndex);

                    for ( String everyDocId : invertedIndex.get(wordInvertedIndex) ) {
                        wordToWrite.append(":" + everyDocId);
                    }

                    int seekLocation = fileIO.writeData(wordToWrite);
                    //System.out.println("Writing : " + wordToWrite + " at : " + seekLocation);

                    trie.add(wordInvertedIndex , seekLocation);

                }

                numberOfPages = 0;
                invertedIndex.clear();
            }

        }
        else if ( endTag.equalsIgnoreCase("title") ) {
            wikiPage.setPageTitile(new String(stringBuilder).toLowerCase());
        }
        else if ( endTag.equalsIgnoreCase("id") ) {
            if ( parentTag.equalsIgnoreCase("page")) {
                wikiPageId = new String(stringBuilder);
                wikiPage.setPageId(wikiPageId);
            }
            else if ( parentTag.equalsIgnoreCase("revision")) {}
            else if ( parentTag.equalsIgnoreCase("contributor")) {}
        }
        else if ( endTag.equalsIgnoreCase("text") ) {
            if ( parentTag.equalsIgnoreCase("revision"))
                wikiPage.setPageText(new String(stringBuilder).toLowerCase());
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        stringBuilder.append(new String(ch,start,length));
    }

    @Override
    public void endDocument() throws SAXException {

        if ( numberOfPages != 0 ) {

            for ( String wordInvertedIndex : invertedIndex.keySet() ) {
                StringBuilder wordToWrite = new StringBuilder(wordInvertedIndex);

                for ( String everyDocId : invertedIndex.get(wordInvertedIndex) ) {
                    wordToWrite.append(":" + everyDocId);
                }

                int seekLocation = fileIO.writeData(wordToWrite);
                trie.add(wordInvertedIndex , seekLocation);

            }

            allStrings.addAll(invertedIndex.keySet());

            numberOfPages = 0;
            invertedIndex.clear();
        }
        fileIO.close();

        //System.out.println("Total  words : " + allStrings.size());

        fileIO.sInitialize();
        fileIO.dumpSecondary(allStrings, trie);
        fileIO.sClose();

    }
}