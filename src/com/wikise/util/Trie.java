package com.wikise.util;

import java.util.*;

public class Trie {

    /* This object will be either :
     *  1. HashMap
     *  2. TreeSet<Integer>
     */
    HashMap<Character, Object> root;

    /**
     *  Default contructor
     */
    public Trie() {
       root = new HashMap<Character, Object>();
    }

    /**
     *  Contructor that takes a String[] of words to add
     *
     *  @param sa a String[] of words to be added
     */
    /*public Trie(String[] sa) {
        this(); 
        addAll(sa);
    }*/

    /**
     *  Constructor that takes a Collection<String> of words to add
     *
     *  @param sc a Collection<String> of words to be added
     */
    /*public Trie(Collection<String> sc) {
        this();
        addAll(sc);
    }*/

    /**
     *  Adds a string to the trie
     * 
     *  @param s String to add to the trie
     */
    private void add(String s) {
        HashMap<Character, Object> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = (HashMap<Character, Object>) curr_node.get(c);
            else {
                curr_node.put(c, new HashMap<Character, HashMap>());
                curr_node = (HashMap<Character, Object>) curr_node.get(c);
            }
        }
        curr_node.put('\0', new HashMap<Character, HashMap>(0)); // term
    }

    /**
     *  Adds a String[] of words to the trie
     * 
     *  @param sa String[] to add to the trie
     */
    private void addAll(String[] sa) {
        for (String s: sa)
            add(s);
    }

    /**
     *  Adds a Collection<String> of words to the trie
     * 
     *  @param sc Collection<String> to add to the trie
     */
    private void addAll(Collection<String> sc) {
        for (String s: sc)
            add(s);
    }

    /** 
     *  Returns TreeSet<Integer> iff the String is in the trie
     *  null otherwise
     *
     *  @param s query
     *  @return TreeSet if the query is in the trie
     */
    public TreeSet<Integer> contains(String s) {
        HashMap<Character, Object> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = (HashMap<Character, Object>) curr_node.get(c);
            else 
                return null;
        }
        if (curr_node.containsKey('#'))
            return (TreeSet<Integer>) curr_node.get('#');
        else 
            return null;
    }

    public void add(String s, int seekLocation) {
        HashMap<Character, Object> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = (HashMap<Character, Object>) curr_node.get(c);
            else {
                curr_node.put(c, new HashMap<Character, HashMap>());
                curr_node = (HashMap<Character, Object>) curr_node.get(c);
            }
        }
        TreeSet<Integer> seekLocations = (TreeSet<Integer>) curr_node.get('#');
        if ( seekLocations == null ) {
            seekLocations = new TreeSet<Integer>();
            seekLocations.add(seekLocation);
            curr_node.put('#', seekLocations ); // term
        }
        else {
            seekLocations.add(seekLocation);
        }
    }
}