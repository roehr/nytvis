package nytvis;

import java.util.ArrayList;
import java.util.List;

public class Article {
	private String Headline;
	private String PublicationDate;
	private int WordCount;
	private List<Keyword> keys;
	private String Newsdesk;
	public Article(){
		keys= new ArrayList<Keyword>();
	}
	
	
	public String getNewsdesk() {
		return Newsdesk;
	}

	public void setNewsdesk(String newsdesk) {
		Newsdesk = newsdesk;
	}

	public void addKey(String name, String value){
		Keyword key= new Keyword();
		key.setName(name);
		key.setValue(value);
		keys.add(key);
	}
	
	public String getHeadline() {
		return Headline;
	}

	public void setHeadline(String headline) {
		Headline = headline;
	}

	public String getPublicationDate() {
		return PublicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		PublicationDate = publicationDate;
	}

	public int getWordCount() {
		return WordCount;
	}

	public void setWordCount(int wordCount) {
		WordCount = wordCount;
	}

	public List<Keyword> getKeys() {
		return keys;
	}

	public void setKeys(List<Keyword> keys) {
		this.keys = keys;
	}

	public void print() {
		System.out.println("Article: " + Headline);
		System.out.println("\t Words: " + WordCount);
		System.out.println("\t Newsdesk: " + Newsdesk);
		System.out.println("\t Date: " + PublicationDate);
		System.out.println("\t Keyswords: ");
		int i = 0;
		while(i< keys.size()){
			System.out.println("\t\t" + keys.get(i).getName() +":" + keys.get(i).getValue());
			i++;
		}
		System.out.println("");
		// TODO Auto-generated method stub
		
	}


}
