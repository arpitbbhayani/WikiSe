WikiSe : Wikipedia Search Engine
====================================
	Arpit Bhayani
	arpit.b.bhayani@gmail.com

A wikipedia search engine built using:
 - Java
 - XML Parsing using SAX Parser.
 - Ranking Algorithms

It works on Wikipedia XML dumps.<br/>
XML Dump Name : enwiki-latest-pages-articles.xml.bz2<br/>
XML Dump Link : http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2<br/>

Implementation basic:<br/>
High level of indexing which reduces the search time.<br/>
The index terms are hashed to characters 'a' - 'z'<br/>
Index is compressed at bitlevel. ( total size = 9.7GB )<br/>
Special infobox parsing to provide direct answeres if possible.<br/>

Special Features :
1. Index compression to make index half of its size. ( bit level compression )
2. Special search fields provided so that user can directly search info infobox.

e.g. Search Query : website:mumbai

Title : mumbai<br/>
Title : mumbai indians<br/>
Title : 2012-13 mumbai f.c. season<br/>
Title : 2008 mumbai attacks<br/>
Title : maharashtra<br/>
Title : public transport in mumbai<br/>
Title : attribution of the 2008 mumbai attacks<br/>
Title : list of constituencies of maharashtra vidhan sabha<br/>
Title : wikipedia:files for deletion/2010 april 13<br/>
Title : list of colleges in mumbai<br/>
****** {{url|www.mcgm.gov.in}} ******				<------ Website link

Interesting search :
pratieik
chudail
joey tribbiani
cartoon

nick:phoebe buffay
powers:batman
age:dimple kapadia
population:amravati
location:takla lake
portrayer:joey tribbiani
series:joey tribbiani
t:priyanka
t:priyanka age:priyanka

Statistics:
 On a mchine of configuration :
	Lenovo Z580 , 4 GB of RAM , 5400rpm hard-disk
 - For 100 MB of data
   - Size of index ( primary+secondary ) : 24.3 MB
   - Time to primary index : 9.031 sec
   - Time to secondary index : 1.041 sec
   - Time to search : 0.007 sec
 - For 46.7 GB of data Wiki XML Dump :
   - Size of index ( primary+secondary ) : 9.7 GB
   - Time to index : 2hr 28min (average)
   - Time to search : 0.251 sec (average on 100 searches)