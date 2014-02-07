package com.wikise.parse;

import com.wikise.process.TermObject;
import com.wikise.process.WikiTextParser;
import com.wikise.util.FileIO;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */
public class WikiSAXParseHandler extends DefaultHandler {

    int numberOfPages = 0;

    /* This stack is used for getting the sequence of the XML tags that are appearing */
    Stack<String> tagStack = null;

    /* This string builder will be used to store the data between the tags */
    StringBuilder stringBuilder = null;

    /* This data structure is used to save the inverted index of the XML dump */
    TreeMap<String , TreeSet<String>> invertedIndex = null;

    /* This is the Trie data structure to save the seek locations of the terms stored in the file */
    /* This approach is not good enough for 43 GB dump so commenting all the Trie code */
    //HashedTrie trie = null;

    /* This variable stores the path of the folder where the index for the XML dump will be stored */
    String indexFolderPath = null;

    /* This data structure saves all all the terms that have occured in XML dump */
    /* This was used to maintain all the terms that have appeared, but now there is no use of it */
    //TreeSet<String> allStrings = new TreeSet<String>();

    /* Variable for WikiPageConcise that save basic information for a Wiki Page */
    WikiPageConcise wikiPage = null;

    /* Stores the ID of the wiki page */
    String wikiPageId = null;

    /* Object of the FileIO that will eventually be used to dump data on the file */
    FileIO fileIO = null;

    /* This variable is used to maintain the count of total temporary files generated */
    private int countTemporaryFile = 0;

    /* Public constructor , but now of no use */
    /*
    public WikiSAXParseHandler(Trie trie, String indexFolderPath) {
        tagStack = new Stack<String>();
        stringBuilder = new StringBuilder();
        invertedIndex = new TreeMap<String, TreeSet<String>>();

        this.trie = trie;
        this.indexFolderPath = indexFolderPath;

        fileIO = new FileIO(indexFolderPath);
        fileIO.initialize();
    }
    */

    public WikiSAXParseHandler(String indexFolderPath) {
        tagStack = new Stack<String>();
        stringBuilder = new StringBuilder();
        invertedIndex = new TreeMap<String, TreeSet<String>>();

        this.indexFolderPath = indexFolderPath;

        fileIO = new FileIO(indexFolderPath);
        fileIO.initialize();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if ( qName.equalsIgnoreCase("page") ) {
            wikiPage = new WikiPageConcise();
        }

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

            //System.out.println(wikiPageId);
            WikiTextParser wikiTextParser = new WikiTextParser(wikiPage , invertedIndex , fileIO);
            //pageMap.put(wikiPage.getPageId() , wikiTextParser.parseText());
            //allStrings.addAll(wikiTextParser.parseText());

            TreeSet<String> tempSet = wikiTextParser.parseText();
            HashMap<String,TermObject> pageTermInfo = wikiTextParser.getPageTermInfo();

            for ( String word : tempSet ) {

                TreeSet<String> docList = invertedIndex.get(word);
                StringBuilder str = new StringBuilder(wikiPageId);
                str.append('$');
                if ( docList == null ) {
                    docList = new TreeSet<String>();
                    //docList.add(wikiPageId + "_" + pageTermInfo.get(word));
                    str.append(pageTermInfo.get(word).toString());
                    docList.add(new String(str));

                    invertedIndex.put(word , docList);
                }
                else {
                    str.append(pageTermInfo.get(word).toString());
                    docList.add(new String(str));
                }

            }

            fileIO.dumpMetaInformation(wikiPage , wikiTextParser.getInfoboxSeekLocation());

            numberOfPages ++;

            if ( numberOfPages == 1000 ) {

                // Uncomment when want old working code :
                // allStrings.addAll(invertedIndex.keySet());

                for ( String wordInvertedIndex : invertedIndex.keySet() ) {
                    StringBuilder wordToWrite = new StringBuilder(wordInvertedIndex);

                    for ( String everyDocId : invertedIndex.get(wordInvertedIndex) ) {
                        wordToWrite.append(":" + everyDocId);
                    }

                    wordToWrite.append('\n');
                    fileIO.writeDataToTemporaryFile(wordToWrite, countTemporaryFile);

                    // Uncomment when want old working code :
                    //trie.add(wordInvertedIndex , seekLocation);

                }

                fileIO.dumpTemporaryFileToDisk();
                countTemporaryFile ++;
                numberOfPages = 0;
                invertedIndex.clear();

            }

        }
        else if ( endTag.equalsIgnoreCase("title") ) {
            wikiPage.setPageTitle(new String(stringBuilder).toLowerCase());
        }
        else if ( endTag.equalsIgnoreCase("id") ) {
            if ( parentTag.equalsIgnoreCase("page")) {
                wikiPageId = new String(stringBuilder);
                wikiPage.setPageId(wikiPageId);
            }
            /* Only ID of the PAGE is required so skipping all others */
            //else if ( parentTag.equalsIgnoreCase("revision")) {}
            //else if ( parentTag.equalsIgnoreCase("contributor")) {}
        }
        else if ( endTag.equalsIgnoreCase("text") ) {
            if ( parentTag.equalsIgnoreCase("revision")) {
                wikiPage.setPageText(new String(stringBuilder).toLowerCase());
            }
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

                wordToWrite.append('\n');

                //System.out.println("Writing : " + wordToWrite);
                fileIO.writeDataToTemporaryFile(wordToWrite, countTemporaryFile);

                // Uncomment when want old working code :
                //trie.add(wordInvertedIndex , seekLocation);

            }

            fileIO.dumpTemporaryFileToDisk();
            countTemporaryFile ++;
            numberOfPages = 0;
            invertedIndex.clear();
        }

        fileIO.close();

        try {
            fileIO.mergeTemporaryFiles(countTemporaryFile);
        }
        catch (IOException e) {
            System.out.println("[[Exception]] :: Exception while Merging files.");
            e.printStackTrace();
        }

        // Uncomment these 3 lines when want old working code :
        //fileIO.sInitialize();
        //fileIO.dumpSecondary(allStrings, trie);
        //fileIO.sClose();

    }
}