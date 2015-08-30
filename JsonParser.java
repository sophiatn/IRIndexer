/*
 * Project 3 - Index
 * Sophia Nguyen
 * 
 * Written for CS 121 - Spring 2015
 * Last Modified: 18 May 2015
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Character;

public class JsonParser {
	
	//json titles
	private static String textTitle = "{\"text\":";
	private static String urlTitle = "\"_id\":";
	private static String htmlTitle = "\"html\":";
	private static String titleTitle = "\"title\":";
	private static String idTitle = "\"id\":";
	
	private File newFile;
	private String url;
	private String rawHTML;
	private String text;
	private String idNumber;
	private String pageTitle;
	private String currentWord;
	private ArrayList<String> tokens;
	private double docId;
	Scanner in;
	
	public JsonParser()
	{
		newFile = null;
		url = null;
		rawHTML = null;
		text = null;
		pageTitle = null;
		idNumber = null;
		currentWord = null;
		tokens = new ArrayList<String>();
	}
	
	public JsonParser(String fileName) throws FileNotFoundException
	{
		newFile = new File(fileName);
		url = null;
		rawHTML = null;
		text = null;
		pageTitle = null;
		idNumber = null;
		currentWord = null;
		tokens = new ArrayList<String>();
		
	}
	
	public void resetParser(String fileName) throws FileNotFoundException
	{
		newFile = new File(fileName);
		url = null;
		rawHTML = null;
		text = null;
		pageTitle = null;
		idNumber = null;
		currentWord = null;
		tokens.clear();
		parseFile(); 
		if (!text.isEmpty())
		{
			tokenizeText();
		}
	}
	public void parseFile() throws FileNotFoundException
	{
		in = new Scanner(newFile);
		while (in.hasNext())
		{
			currentWord = in.next();
			if (currentWord.equals(textTitle))
			{
				currentWord = in.next();
				parseText();
			}
			if (currentWord.equals(urlTitle))
			{
				currentWord = in.next();
				parseUrl();
			}
			if (currentWord.equals(titleTitle))
			{
				currentWord = in.next();
				parseTitle();
			}
			if (currentWord.equals(htmlTitle))
			{
				currentWord = in.next();
				parseHtml();
			}
			if (currentWord.equals(idTitle))
			{
				currentWord = in.next();
				parseId();

			}
			
			
		}
		in.close();
	}
	private void tokenizeText()
	{
		String newToken = "";
		int length = text.length();
		int currentPosition = 0;
		char currentChar;
		
		while  (currentPosition < length)
		{
			currentChar = text.charAt(currentPosition);
			while (Character.isLetter(currentChar) && currentPosition < length)
			{
				newToken = newToken + currentChar;
				currentPosition++;
				if (currentPosition < length)
				{
					currentChar = text.charAt(currentPosition);		
				}
			}
			if (!newToken.isEmpty())
			{
				tokens.add(newToken);
				newToken = "";
			}
			while ((!Character.isLetter(currentChar)) && currentPosition < length)
			{
				currentPosition++;
				if (currentPosition < length)
				{
					currentChar = text.charAt(currentPosition);		
				}
			}
		}
	}
	public ArrayList<String> getTokens()
	{
		return tokens;
	}
	public void printTokens()
	{
		int size = tokens.size();
		for (int i = 0; i < size; i++)
		{
			System.out.println(tokens.get(i));
		}
				
	}
	private void parseText()
	{
		String newText = "";
		
		while (!currentWord.equals(urlTitle) && in.hasNext())
		{
			newText = newText + currentWord.toLowerCase() + " ";
			currentWord = in.next();
			
			
		}
		setText(newText);
		
	}
	private void parseUrl()
	{
		
		String newUrl = currentWord;
		setUrl(newUrl);
		currentWord = in.next();
		
	}
	private void parseTitle()
	{
		String newTitle = "";
		while (!currentWord.equals(htmlTitle) && in.hasNext())
		{
			newTitle = newTitle + currentWord + " ";
			currentWord = in.next();
		}
		setPageTitle(newTitle);
	}
	private void parseHtml()
	{
		//String newHtml = "";
		while (!currentWord.equals(idTitle) && in.hasNext())
		{
			//newHtml = newHtml + currentWord + " ";
			currentWord = in.next();
		}
		//setHtml(newHtml);
	}
	private void parseId()
	{
		setIdNumber(currentWord);
	}
	
	private void setUrl(String newUrl)
	{
		int length = newUrl.length();
		url = newUrl.substring(1, length - 2);
	}
	private void setText(String newText)
	{
		int length = newText.length();
		text = newText.substring(1, length - 3);
	}
	private void setPageTitle(String title)
	{
		int length = title.length();
		pageTitle = title.substring(1, length - 3);
	}
	private void setHtml(String newHtml)
	{
		int length = newHtml.length();
		rawHTML = newHtml.substring(1, length - 3);
	}
	private void setIdNumber(String newId)
	{
		int length = newId.length();
		idNumber = newId.substring(0, length - 1);
	}
	public String getText()
	{
		return text;
	}
	public String getUrl()
	{
		return url;
	}
	public String getTitle()
	{
		return pageTitle;
	}
	public String getHtml()
	{
		return rawHTML;
	}
	public String getIdNumber()
	{
		return idNumber;
	}
	public double getDocId()
	{
		docId = Double.parseDouble(idNumber);
		return docId;
				
	}
	public void printParse()
	{
		System.out.println("text: " + text);
		System.out.println("url: " + url);
		System.out.println("title: " + pageTitle);
		System.out.println("html: " + rawHTML);
		System.out.println("id: " + idNumber);
	}
	
	
 }
    
