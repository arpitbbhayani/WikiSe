package com.wikise.search;

import com.wikise.search.Stemmer;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Arpit Bhayani on 13/1/14.
 */
public class Classifiers {

    public static HashSet<String> stopWordsSet = null;
    public static HashMap<String,String> mostFreqWithStemming = null;

    private static Stemmer stemmer = null;

    public static void initialize() {

        stemmer = new Stemmer();
        mostFreqWithStemming = new HashMap<String, String>();

    }

    public static String getStemmedWord( String word ) {

        String stemmed = mostFreqWithStemming.get(word);

        if ( stemmed != null ) {
            return stemmed;
        }

        stemmer.add(word.toCharArray() , word.length());

        return stemmer.stem();

    }

    public static boolean isStopword(String word) {

        if ( stopWordsSet == null ) {
            fillStopWords();
        }
        return stopWordsSet.contains(word);
    }

    private static void fillStopWords() {

        stopWordsSet = new HashSet<String>();

        stopWordsSet.add("a");
        stopWordsSet.add("able");
        stopWordsSet.add("about");
        stopWordsSet.add("across");
        stopWordsSet.add("after");
        stopWordsSet.add("all");
        stopWordsSet.add("almost");
        stopWordsSet.add("also");
        stopWordsSet.add("am");
        stopWordsSet.add("among");
        stopWordsSet.add("an");
        stopWordsSet.add("and");
        stopWordsSet.add("any");
        stopWordsSet.add("are");
        stopWordsSet.add("as");
        stopWordsSet.add("at");
        stopWordsSet.add("be");
        stopWordsSet.add("because");
        stopWordsSet.add("been");
        stopWordsSet.add("but");
        stopWordsSet.add("by");
        stopWordsSet.add("can");
        stopWordsSet.add("cannot");
        stopWordsSet.add("could");
        stopWordsSet.add("dear");
        stopWordsSet.add("did");
        stopWordsSet.add("do");
        stopWordsSet.add("does");
        stopWordsSet.add("either");
        stopWordsSet.add("else");
        stopWordsSet.add("ever");
        stopWordsSet.add("every");
        stopWordsSet.add("for");
        stopWordsSet.add("from");
        stopWordsSet.add("get");
        stopWordsSet.add("got");
        stopWordsSet.add("had");
        stopWordsSet.add("has");
        stopWordsSet.add("have");
        stopWordsSet.add("he");
        stopWordsSet.add("her");
        stopWordsSet.add("hers");
        stopWordsSet.add("him");
        stopWordsSet.add("his");
        stopWordsSet.add("how");
        stopWordsSet.add("however");
        stopWordsSet.add("i");
        stopWordsSet.add("if");
        stopWordsSet.add("in");
        stopWordsSet.add("into");
        stopWordsSet.add("is");
        stopWordsSet.add("it");
        stopWordsSet.add("its");
        stopWordsSet.add("just");
        stopWordsSet.add("least");
        stopWordsSet.add("let");
        stopWordsSet.add("like");
        stopWordsSet.add("likely");
        stopWordsSet.add("may");
        stopWordsSet.add("me");
        stopWordsSet.add("might");
        stopWordsSet.add("most");
        stopWordsSet.add("must");
        stopWordsSet.add("my");
        stopWordsSet.add("neither");
        stopWordsSet.add("no");
        stopWordsSet.add("nor");
        stopWordsSet.add("not");
        stopWordsSet.add("of");
        stopWordsSet.add("off");
        stopWordsSet.add("often");
        stopWordsSet.add("on");
        stopWordsSet.add("only");
        stopWordsSet.add("or");
        stopWordsSet.add("other");
        stopWordsSet.add("our");
        stopWordsSet.add("own");
        stopWordsSet.add("rather");
        stopWordsSet.add("said");
        stopWordsSet.add("say");
        stopWordsSet.add("says");
        stopWordsSet.add("she");
        stopWordsSet.add("should");
        stopWordsSet.add("since");
        stopWordsSet.add("so");
        stopWordsSet.add("some");
        stopWordsSet.add("than");
        stopWordsSet.add("that");
        stopWordsSet.add("the");
        stopWordsSet.add("their");
        stopWordsSet.add("them");
        stopWordsSet.add("then");
        stopWordsSet.add("there");
        stopWordsSet.add("these");
        stopWordsSet.add("they");
        stopWordsSet.add("this");
        stopWordsSet.add("tis");
        stopWordsSet.add("to");
        stopWordsSet.add("too");
        stopWordsSet.add("twas");
        stopWordsSet.add("us");
        stopWordsSet.add("wants");
        stopWordsSet.add("was");
        stopWordsSet.add("we");
        stopWordsSet.add("were");
        stopWordsSet.add("what");
        stopWordsSet.add("when");
        stopWordsSet.add("where");
        stopWordsSet.add("which");
        stopWordsSet.add("while");
        stopWordsSet.add("who");
        stopWordsSet.add("whom");
        stopWordsSet.add("why");
        stopWordsSet.add("will");
        stopWordsSet.add("with");
        stopWordsSet.add("would");
        stopWordsSet.add("yet");
        stopWordsSet.add("you");
        stopWordsSet.add("your");
    }

}
