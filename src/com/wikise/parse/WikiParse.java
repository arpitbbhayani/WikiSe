package com.wikise.parse;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */

import com.wikise.util.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.TreeSet;

/**
 * This class is parses Wikipedia XML dump.
 * It uses SAX Parser.
 * {@link "http://www.saxproject.org/"}
 */
public class WikiParse {

    String filePath = null;
    Trie trie = null;

    /**
     *  This is the constructor for WikiParse
     *  @param filePath This is the path of the XML file to be parsed
     *                  The document needs to be a proper Wikipedia dump.
     */
    public WikiParse( String filePath ) {
        /* Default Constructor */
        this.filePath = filePath;
        this.trie = new Trie();
    }


    /**
     * Initiation of parsing process.
     * Parsing is done Page By Page.
     * Dumping the data into a file is made thread safe.
     * @param indexFolderPath
     */
    public void parse(String indexFolderPath) {

        Classifiers.initialize();

        try {

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            WikiSAXParseHandler wikiSAXParseHandler = new WikiSAXParseHandler(trie , indexFolderPath);

            saxParser.parse(filePath , wikiSAXParseHandler );

        } catch (ParserConfigurationException e) {
            //e.printStackTrace();
            System.out.println("Parser config exception");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
