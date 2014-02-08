package com.wikise.util;

/**
 * Created by Arpit Bhayani on 8/2/14.
 */
import java.util.*;

public class Trie {

    HashMap<Character, HashMap> root;

    /**
     *  Default contructor
     */
    public Trie() {
        root = new HashMap<Character, HashMap>();
    }

    /**
     *  Contructor that takes a String[] of words to add
     *
     *  @param sa a String[] of words to be added
     */
    public Trie(String[] sa) {
        this();
        addAll(sa);
    }

    /**
     *  Constructor that takes a Collection<String> of words to add
     *
     *  @param sc a Collection<String> of words to be added
     */
    public Trie(Collection<String> sc) {
        this();
        addAll(sc);
    }

    /**
     *  Adds a string to the trie
     *
     *  @param s String to add to the trie
     */
    public void add(String s) {
        HashMap<Character, HashMap> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = curr_node.get(c);
            else {
                curr_node.put(c, new HashMap<Character, HashMap>());
                curr_node = curr_node.get(c);
            }
        }
        curr_node.put('\0', new HashMap<Character, HashMap>(0)); // term
    }

    /**
     *  Adds a String[] of words to the trie
     *
     *  @param sa String[] to add to the trie
     */
    public void addAll(String[] sa) {
        for (String s: sa)
            add(s);
    }

    /**
     *  Adds a Collection<String> of words to the trie
     *
     *  @param sc Collection<String> to add to the trie
     */
    public void addAll(Collection<String> sc) {
        for (String s: sc)
            add(s);
    }

    /**
     *  Returns true iff the String is in the trie
     *
     *  @param s query
     *  @return true if the query is in the trie
     */
    public boolean contains(String s) {
        HashMap<Character, HashMap> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = curr_node.get(c);
            else
                return false;
        }
        if (curr_node.containsKey('\0'))
            return true;
        else
            return false;
    }

}