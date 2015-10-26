package nytvis;

import java.io.BufferedWriter;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Model {
	private int totalWords = 0;
	private List<Article> elements = new ArrayList<Article>();
	private List<String>  NewsDesks = new ArrayList<String>();
	private List<Article>  SubList = new ArrayList<Article>();
	private String articles;
	private int SubCount = 0;
	private File file;
	public List<Article> getElements(){
		return elements;
	}
	
	
	
	public List<String> getNewsDesks() {
		return NewsDesks;
	}



	public void setNewsDesks(List<String> newsDesks) {
		NewsDesks = newsDesks;
	}






	public List<Article> getSubList() {
		return SubList;
	}






	public void setSubList(List<Article> subList) {
		SubList = subList;
	}






	public int getSubCount() {
		return SubCount;
	}






	public void setSubCount(int subCount) {
		SubCount = subCount;
	}






	public File getFile() {
		return file;
	}






	public void setFile(File file) {
		this.file = file;
	}






	public void setTotalWords(int totalWords) {
		this.totalWords = totalWords;
	}






	public void setElements(List<Article> elements) {
		this.elements = elements;
	}




	public void addArticle(Article art){
		elements.add(art);
		totalWords += art.getWordCount();
		
	}
	public void printall() {
		Iterator<Article> iter=this.elements.iterator();
		while (iter.hasNext()) {
			Article art =  iter.next();
			art.print();
			
		}
	}
	
	public int getTotalWords() {
		return totalWords;
	}

	public void NewsDeskListing(){
		Iterator<Article> iter=this.elements.iterator();
		while (iter.hasNext()) {
			Article art =  iter.next();
			if(!insideNewsDesks(art.getNewsdesk())){
				NewsDesks.add(art.getNewsdesk());
			}
			
		}
		
	}


	private boolean insideNewsDesks(String newsdesk) {
		Iterator<String> iter=this.NewsDesks.iterator();
		while(iter.hasNext()){
			String news= iter.next();
			if (news.equals(newsdesk))
			{
				return true;
			 }
		}
		return false;
	}
	
	void generateDesks(){
		System.out.println("generating Desks...");
		NewsDesks= new ArrayList<String>();
		Iterator<Article> iter= elements.iterator();
		while(iter.hasNext()){
			String news= iter.next().getNewsdesk();
			NewsDesks.add(news);
		}
	}
	
	public void generateSublist(String news){
		
		SubList= new ArrayList<Article>();
		SubList.clear();
		Iterator<Article> iter=this.elements.iterator();
		while (iter.hasNext()) {
			Article art =  iter.next();
			if(news.equals(art.getNewsdesk())){
				SubList.add(art);
				SubCount += art.getWordCount();
			}
			
		}
	}
}
