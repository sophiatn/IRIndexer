/*
 * Project 3 - Index
 * 
 * Sophia Nguyen
 * Written for CS 121 - Spring 2015
 * Last Modified: 18 May 2015
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
public class Index {
	public static final int TOTALDOCS = 49955;
	
	private static TreeMap<String, TreeMap<Double, Integer>> wordToDoc = new TreeMap<String, TreeMap<Double, Integer>>();
	private static TreeMap<String, Integer> termToTermId = new TreeMap<String, Integer>(); //map of term to termId
	private static TreeMap<Double, String> docIdToUrl = new TreeMap<Double, String>();
	private static TreeMap<Double, Integer> docToTotalWords = new TreeMap<Double, Integer>();
	private static TreeMap<String, Integer> corpusFrequency = new TreeMap<String, Integer>();
	private static TreeMap<String, TreeMap<Double, Double>> wordToTfIdf = new TreeMap<String, TreeMap<Double, Double>>();
	private static int uniqueTermId = 0; //designates id number to new terms, also number of total unique terms
	private static JsonParser parser = new JsonParser();
	private static ArrayList<String> currentTokens = new ArrayList<String>();
	
	public static void processDoc(String fileName) throws FileNotFoundException
	{
		parser.resetParser(fileName);
		double docId = parser.getDocId();
		String url = parser.getUrl();
		docIdToUrl.put(docId, url);
		currentTokens = parser.getTokens();
		int size = currentTokens.size();
		docToTotalWords.put(docId,  size);
		String word;
		int frequency = 0;
		int corpusFrequencyCount = 0;
		
		for (int i = 0; i < size; i++)
		{
			word = currentTokens.get(i);
			if (wordToDoc.containsKey(word))
			{
				if (wordToDoc.get(word).containsKey(docId)) //if this doc is already recorded for this word
				{
					frequency = wordToDoc.get(word).get(docId);
					frequency++;
					wordToDoc.get(word).replace(docId, frequency);
				}
				else //if this word exists already, but not in this doc
				{
					frequency = 1;
					wordToDoc.get(word).put(docId, frequency);
				}
				corpusFrequencyCount = corpusFrequency.get(word);
				corpusFrequencyCount++;
				corpusFrequency.put(word, corpusFrequencyCount);
			}
			else //if this is a brand new word
			{
				uniqueTermId++;
				termToTermId.put(word, uniqueTermId);
				TreeMap<Double, Integer> newMap = new TreeMap<Double,Integer>();
				frequency = 1;
				newMap.put(docId, frequency);
				wordToDoc.put(word, newMap);
				corpusFrequencyCount = 1;
				corpusFrequency.put(word,  corpusFrequencyCount);
			}
		}
		
	}
	public static void printMap(TreeMap<Double, Integer> newMap)
	{
		int size = newMap.size();
		int commaCounter = 0;
		for (Map.Entry<Double, Integer> entry : newMap.entrySet())
		{
			System.out.print("(" + entry.getKey() + ", " + entry.getValue() + ")");
			commaCounter++;
			if (commaCounter < size)
			{
				System.out.print(", ");
			}
		}
	}
	public static void printOtherMap(TreeMap<Double, Double> newMap)
	{
		int size = newMap.size();
		int commaCounter = 0;
		for (Map.Entry<Double, Double> entry : newMap.entrySet())
		{
			System.out.print("(" + entry.getKey() + ", "); // + entry.getValue() + ")");
			System.out.printf("%.9f", entry.getValue());
			System.out.print(")");
			commaCounter++;
			if (commaCounter < size)
			{
				System.out.print(", ");
			}
		}
	}
	public static void printIndex()
	{
		int counter = 0;
		for (Map.Entry<String, TreeMap<Double, Integer>> entry : wordToDoc.entrySet())
		{
			counter++;
			System.out.print(counter + ") ");

			System.out.print(entry.getKey() + ": {");
			printMap(entry.getValue());
			System.out.print("}\n");
			System.out.println();
			
		}
	}
	//This function computes the TfIdf of each word, for each document.
	//It can only be run after the whole corpus has been processed because it needs
	//to know the number of total documents with the term in it
	public static double computeTfIdf(String word, double docId)
	{
		//TF(t) = (Number of times term t appears in a document) / (Total number of terms in the document)
		double frequencyInDoc = getDocFrequency(word, docId);
		double totalTerms = getDocTotalWords(docId);
		double termFrequency = frequencyInDoc / totalTerms;
		
		//IDF(t) = log_e(Total number of documents / Number of documents with term t in it)
		double numDocs = getNumDocsWithTerm(word);
		double docFreq = TOTALDOCS / numDocs;
		double inverseDocFreq = Math.log(docFreq);
		
		double tfIdf = termFrequency * inverseDocFreq;
		
		if (tfIdf >= 0 && tfIdf < Double.MAX_VALUE)
		{
			return tfIdf;
		}
		else
		{
			return 0;
		}
		
	}
	public static void createTfIdfMap()
	{
		TreeMap<Double, Double> docToTfIdf; 
		TreeMap<Double, Integer> current;
		for (Map.Entry<String, TreeMap<Double, Integer>> entry : wordToDoc.entrySet())
		{
			String newKey = entry.getKey();
			docToTfIdf = new TreeMap<Double, Double>();
			current = entry.getValue();
			for (Map.Entry<Double, Integer> mapEntry : current.entrySet())
			{
				double currentDocId = mapEntry.getKey();
				double tfIdfValue = computeTfIdf(newKey, currentDocId); 
				docToTfIdf.put(currentDocId, tfIdfValue);
			}
			wordToTfIdf.put(newKey, docToTfIdf); 
			
		}
	}
	public static void printTfIdfIndex()
	{
		int counter = 0;
		for (Map.Entry<String, TreeMap<Double, Double>> entry : wordToTfIdf.entrySet())
		{
			counter++;
			System.out.print(counter + ") ");

			System.out.print(entry.getKey() + ": {");
			printOtherMap(entry.getValue());
			System.out.print("}\n");
			System.out.println();
			
		}
	}
	public static int getNumDocsWithTerm(String word)
	{
		int numDocs = 0;
		if (wordToDoc.containsKey(word))
		{
			numDocs = wordToDoc.get(word).size();
		}
		return numDocs;
	}
	public static int getDocFrequency(String word, double docId)
	{
		int docFrequency = 0;
		if (wordToDoc.containsKey(word))
		{
			if (wordToDoc.get(word).containsKey(docId))
			{
				docFrequency = wordToDoc.get(word).get(docId);
			}
		}
		return docFrequency;
	}
	public static int getDocTotalWords(double docId)
	{
		if (docToTotalWords.containsKey(docId))
		{
			return docToTotalWords.get(docId);
		}
		else
		{
			return -1;
		}
	}
	public static int getCorpusFrequencyCount(String word)
	{
		if (corpusFrequency.containsKey(word))
		{
			return corpusFrequency.get(word);
		}
		else
		{
			return -1;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		
		
		
			String[] files = new File(args[0]).list();
		    String dirName = args[0] + "\\";
		    String fileName = null;
		    for (String file : files)
		    {
		    	fileName = dirName + file;
		    	if (fileName.endsWith(".txt"))
		    	{
		    		System.out.println(fileName);
		    		processDoc(fileName);
		    	}

		    		
		    }
		    printIndex();
		    //createTfIdfMap(); 
		    //printTfIdfIndex(); 


	}

}
