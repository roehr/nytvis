package nytvis.stack;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import nytvis.Article;

public class Stackitem {
	private String keyword;
	private Map<String, StackNDentry> valuesperDesk = new HashMap<String, StackNDentry>();
	private int sumvalue=0;
	private int x1=0;
	private int x2=0;
	private int y1=0;
	private int y2=0;
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getY2() {
		return y2;
	}
	public void setY2(int y2) {
		this.y2 = y2;
	}
	private int refy=0;
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Map<String, StackNDentry> getValuesperDesk() {
		return valuesperDesk;
	}
	public void setValuesperDesk(Map<String, StackNDentry> valuesperDesk) {
		this.valuesperDesk = valuesperDesk;
	}
	public int getMaxvalue() {
		return sumvalue;
	}
	public void setMaxvalue(int maxvalue) {
		this.sumvalue = maxvalue;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public int getRefy() {
		return refy;
	}
	public void setRefy(int refy) {
		this.refy = refy;
	}
	Stackitem(String key, String nd){
		keyword= key;
		StackNDentry s = new StackNDentry(nd,1);
		valuesperDesk.put(nd, s);
		sumvalue=1;		
	}
    
	public void updateNDEntries(String newsdesk) {
		if(valuesperDesk.containsKey(newsdesk)){
			int value = valuesperDesk.get(newsdesk).getValue();
			value++;
			valuesperDesk.get(newsdesk).setValue(value);
			sumvalue++;
		}
		else{
			StackNDentry s = new StackNDentry(newsdesk,1);
			valuesperDesk.put(newsdesk, s);
			sumvalue++;
		}
		
	}
	public void computendpos(int y0, int y1) {
		int height= y0-y1;
		int ypos=y0;
		int sumofdeskweights = 0;
		StackNDentry entry;
		StackNDentry lastentry=null;
		for(String k : valuesperDesk.keySet()){
			sumofdeskweights+= valuesperDesk.get(k).getValue();
		}
		for(String k : valuesperDesk.keySet()){
			entry= valuesperDesk.get(k);
			entry.setY1(y0);
			entry.setY2(y0 - height* entry.getValue()/sumofdeskweights);
			y0=entry.getY2();
			lastentry=entry;
		}
		lastentry.setY2(ypos-height);
		
	}
	
	
}
