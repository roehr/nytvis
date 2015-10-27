package nytvis.timeview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nytvis.Article;
import nytvis.Keyword;
import nytvis.Model;

public class TimelineView  {
	private Model model = null;
	private Map<String, List<Pair>> items= null;
	private LocalDate datemax = null;
	private LocalDate datemin = null;
	private int maxval=1;
	public TimelineView(Model m) throws ParseException{
		model=m;
		prepareData();
	}
	public int getMaxval(){
		return maxval;
	}
	public void prepareData() throws ParseException{
		//for this View, we're interested in Date and Keywords - let's parse them from the model
		Iterator<Article> ait= model.getElements().iterator();
		items= new HashMap<String, List<Pair>>();
		String refdate=  model.getElements().get(0).getPublicationDate();
		refdate=refdate.substring(0, 10);
	
		
		datemax = LocalDate.parse(refdate);
		datemin= LocalDate.parse(refdate);
		while(ait.hasNext()){
			Article art= ait.next();
			
			LocalDate d= LocalDate.parse(art.getPublicationDate().substring(0, 10));
			if(d.isBefore(datemin)){
				datemin=d;
			}
			if(d.isAfter(datemax)){
				datemax=d;
			}
			//System.out.println(sdfmt.format(d));
			Iterator<Keyword> kit= art.getKeys().iterator();
			while(kit.hasNext()){
				String k = kit.next().getValue();
				//key already in list?
				if(items.containsKey(k)){
					//date already in there?
					boolean inhere = false;
					for(int i=0; i<items.get(k).size(); i++){
						if(items.get(k).get(i).getDate().equals(d)){
							inhere=true;
							int v= items.get(k).get(i).getValue();
							v++;
							items.get(k).get(i).setValue(v);
							if(v>maxval){maxval=v;}
							break;
						}
					}
					if(!inhere){
						items.get(k).add(new Pair(d,1));
					}
					
				}
				//Nope? add it now
				else{
					Pair p = new Pair(d , 1);
					List<Pair> plist= new ArrayList<Pair>();
					plist.add(p);
					items.put(k, plist);
					
				}
			}
			
			
		}
		
		
	}
	

	
	public Map<String, List<Pair>> getItems() {
		return items;
	}

	public void setItems(Map<String, List<Pair>> items) {
		this.items = items;
	}

	public LocalDate getDatemax() {
		return datemax;
	}

	public void setDatemax(LocalDate datemax) {
		this.datemax = datemax;
	}

	public LocalDate getDatemin() {
		return datemin;
	}

	public void setDatemin(LocalDate datemin) {
		this.datemin = datemin;
	}

	public void print(){

		for (String k : items.keySet()) {
		    System.out.println(k + ":");
		    List<Pair> plist= items.get(k);
		    Iterator<Pair> pit= plist.iterator();
		    while(pit.hasNext()){
		    	pit.next().print();
		    }
		}
	}
	
}
