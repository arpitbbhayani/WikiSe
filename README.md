WikiSe
======

A wikipedia search engine built using:
 - Java
 - XML Parsing using SAX Parser.
 - Ranking Algorithms

It works on Wikipedia XML dumps.

Procedure:
 - Stopword removal
 - Stemming
 - Primary indexing
 - Secondary indexing

Statistics:
 On a mchine of configuration :
	Lenovo Z580 , 4 GB of RAM , 5400rpm hard-disk
 - For 100 MB of data
   - Size of index ( primary+secondary ) : 24.3 MB
   - Time to primary index : 9.031 sec
   - Time to secondary index : 1.041 sec
   - Time to search : 0.007 sec

Author:
Arpit Bhyanai
