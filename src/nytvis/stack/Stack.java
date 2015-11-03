package nytvis.stack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mcavallo.opencloud.Tag;

import nytvis.Article;
import nytvis.Keyword;
import nytvis.Model;
import nytvis.wordcloud.WordCloudView;

public class Stack {
	private int minscore = 10;
	private WordCloudView wcv;
	private Model model=null;
	private int width;
	private int height;
	private int startx;
	private int starty;
	private int maxvalue=0;
	private LocalDate datemin;
	private LocalDate datemax;
	Map<LocalDate, List<Stackitem>> stack = new TreeMap<LocalDate, List<Stackitem>>(new Comparator<LocalDate>() {
	    public int compare(LocalDate date1, LocalDate date2) {
	        return date2.compareTo(date1);
	    }
	});
	public Stack(Model m, WordCloudView wv, int x,int y,int w,int h){
		wcv=wv;
		model = m;
		prepareModel();
		maxvalue= keymaxvalue();
		startx=x;
		starty=y;
		width=w;
		height=h;
		computestackvalues();
		
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getStartx() {
		return startx;
	}

	public void setStartx(int startx) {
		this.startx = startx;
	}

	public int getStarty() {
		return starty;
	}

	public void setStarty(int starty) {
		this.starty = starty;
	}

	public int getMaxvalue() {
		return maxvalue;
	}

	public void setMaxvalue(int maxvalue) {
		this.maxvalue = maxvalue;
	}

	public LocalDate getDatemin() {
		return datemin;
	}

	public void setDatemin(LocalDate datemin) {
		this.datemin = datemin;
	}

	public LocalDate getDatemax() {
		return datemax;
	}

	public void setDatemax(LocalDate datemax) {
		this.datemax = datemax;
	}

	public Map<LocalDate, List<Stackitem>> getStack() {
		return stack;
	}

	public void setStack(Map<LocalDate, List<Stackitem>> stack) {
		this.stack = stack;
	}

	private int keymaxvalue(){
		int max=0;
		for (LocalDate k : stack.keySet()) {
			Iterator<Stackitem> sit= stack.get(k).iterator();
			while(sit.hasNext()){
				Stackitem si= sit.next();
				max=Math.max(si.getMaxvalue(), max);
			}	
			
		}
		return max;
		
	}
	private void computestackvalues() {
		int i=0;
		for (LocalDate k : stack.keySet()) {
			Iterator<Stackitem> sit= stack.get(k).iterator();
			int j=0;
			while(sit.hasNext()){
				Stackitem si= sit.next();
				//compute the positions for every Item
				double y0=(double) starty;
				double y1=(double) starty - (double)height*(double)si.getMaxvalue()/(double)maxvalue;
				double widthofday=(double)width/(double)(stack.size());
				double widthofkey=widthofday/(double)stack.get(k).size();
				double x0=(double)startx+i*widthofday+j*widthofkey;
				double x1=x0+widthofkey;
				si.setX1((int)x0);
				si.setX2((int)x1);
				si.setY1((int)y0);
				si.setY2((int)y1);
				si.computendpos((int)y0,(int)y1);
				j++;
			}		
			
			i++;
		}
		
	}

	private void prepareModel() {
		String refdate=  model.getElements().get(0).getPublicationDate();
		refdate=refdate.substring(0, 10);
		LocalDate ref= LocalDate.parse(refdate);
		datemin=ref;
		datemax=ref;
	
		Iterator<Article> ait = model.getElements().iterator();
		while(ait.hasNext()){
			Article a = ait.next(); 
			String date= a.getPublicationDate();
			date=date.substring(0, 10);
			LocalDate key= LocalDate.parse(date);
			
			if(stack.containsKey(key)){
				//check if there is already an entry for the keywords
				Iterator<Keyword> kit = a.getKeys().iterator();
				while(kit.hasNext()){
					boolean good = true;
					String keyword= kit.next().getValue();
					//check if keyword only is there once...
					 for (Tag tag : wcv.getCloud().tags()){
						 if(keyword.equals(tag.getName())){
							 if(tag.getScoreInt()<minscore)
							 {
								 good=false;
							 }
							 break;
						 }
					 }
					boolean found= false;
					Iterator<Stackitem> sit= stack.get(key).iterator();
					while(sit.hasNext()){
						Stackitem actitem= sit.next();
						
						if(actitem.getKeyword().equals(keyword)){
							//keyword already has an entry, update values of NDS:
							actitem.updateNDEntries(a.getNewsdesk());
				       	    found=true;	
						}
					}
					if(found==false&&good){
						Stackitem item= new Stackitem(keyword, a.getNewsdesk());
						stack.get(key).add(item);
					}
				}
				
				
			}
			else{
				List<Stackitem> stacklist= new ArrayList<Stackitem>();
				Iterator<Keyword> kit= a.getKeys().iterator();
				while(kit.hasNext()){
					Stackitem item = new Stackitem(kit.next().getValue(), a.getNewsdesk());
					boolean good=true;
					 for (Tag tag : wcv.getCloud().tags()){
						 if(item.getKeyword().equals(tag.getName())){
							 if(tag.getScoreInt()<minscore)
							 {
								 good=false;
							 }
							 break;
						 }
					 }
					if(good){
					stacklist.add(item);}
				}
				stack.put(key, stacklist);
			}
					
		}
	}
	


	
}
