package com.wikise.parse;

/**
 * Created by Arpit Bhayani on 11/1/14.
 */

import com.wikise.util.Classifiers;
import com.wikise.util.FileIO;
import com.wikise.util.FileReadIO;
import com.wikise.util.Trie;
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
    FileReadIO fileReadIO = null;

    /**
     *  This is the constructor for WikiParse
     *  @param filePath This is the path of the XML file to be parsed
     *                  The document needs to be a proper Wikipedia dump.
     */
    public WikiParse( String filePath ) {
        /* Default Constructor */
        this.filePath = filePath;
        this.trie = new Trie();
        this.fileReadIO = new FileReadIO();
    }


    /**
     * Initiation of parsing process.
     * Parsing is done Page By Page.
     * Dumping the data into a file is made thread safe.
     */
    public void parse() {

        Classifiers.initialize();

        try {

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            WikiSAXParseHandler wikiSAXParseHandler = new WikiSAXParseHandler(trie);

            saxParser.parse(filePath , wikiSAXParseHandler );

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public TreeSet<Integer> search(String searchQuery) {

        TreeSet<Integer> listOfSeeks = trie.contains(Classifiers.getStemmedWord(searchQuery.split(" ")[0].toLowerCase()));
        return fileReadIO.readData(searchQuery.charAt(0) , listOfSeeks);
    }
}
