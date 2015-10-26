package nytvis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.embed.swing.JFXPanel;
import nytvis.timeview.Pair;
import nytvis.timeview.TimelineView;
import nytvis.treemap.ItemBoundaries;
import nytvis.treemap.NewsDeskBoundaries;
import nytvis.treemap.Treemap;
import nytvis.wordcloud.WordCloudView;

public class View extends JFXPanel {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private Model model = null;
	private Treemap treemap=null;
	private List<NewsDeskBoundaries> NDBounds = null;
	private boolean sizeview = false;
	private boolean timeline = false;
	private ItemBoundaries active = null;
	private boolean hasactive = false;
	private boolean activeneedsupdate = false;
	private List<ItemBoundaries> RelatedBounds = null;
	private boolean hasrelateditems = false;
	boolean firstdraw=true;
	HashMap<String, Color> colormap = null;
	private int width;
	private int height;
	private WordCloudView wcv=null;
	public View() {
		width=getWidth();
		height=getHeight();
	}

	public WordCloudView getWcv() {
		return wcv;
	}

	public void setWcv(WordCloudView wcv) {
		this.wcv = wcv;
	}

	@Override
	public void paint(Graphics g) {
		timeline = false;
		if (timeline) {
			try {
				drawTimelineView(g);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			drawTreemap(g);
			
		}

	}

	private void findrelatedArticles(){
		if(hasactive){
			RelatedBounds = new ArrayList<ItemBoundaries>();
			Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
			while(nit.hasNext()){
				Iterator<ItemBoundaries> iit= nit.next().getItembounds().iterator();	
				while(iit.hasNext()){
					ItemBoundaries item = iit.next();
					Iterator<Keyword> ait= item.getArt().getKeys().iterator();{
						while(ait.hasNext()){
							Iterator<Keyword> kit = active.getArt().getKeys().iterator();
							Keyword a= ait.next();
							while(kit.hasNext()){
								if(kit.next().getValue().equals(a.getValue())){
									RelatedBounds.add(item);
									hasrelateditems=true;
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean isSizeview() {
		return sizeview;
	}

	public void setSizeview(boolean sizeview) {
		this.sizeview = sizeview;
	}

	public boolean isHasactive() {
		return hasactive;
	}

	public void setHasactive(boolean hasactive) {
		this.hasactive = hasactive;
	}
	public void resetTreemap(){
		treemap.setModel(model);
		treemap.squarifynd();
		wcv.setModel(model);
		wcv.generateKeyWords();
		wcv.draw();
		
	}
	public void removeDeskfromTreemap(){
		if(hasactive){
			hasactive=false;
			Model treemapmodel=new Model();
			Iterator<Article> ait = treemap.getModel().getElements().iterator();
			while(ait.hasNext()){
				Article a= ait.next();
				if(a.getNewsdesk().equals(active.getArt().getNewsdesk())){
					
				}
				else{
				treemapmodel.addArticle(a);	
				}
			}
			
			treemap.setModel(treemapmodel);
			treemap.squarifynd();
			wcv.setModel(treemapmodel);
			wcv.generateKeyWords();
			wcv.draw();
			
		}
	
	}
	private void drawTreemap(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;
		g2D.clearRect(0, 0, getWidth(), getHeight());
		double w = (double) getWidth();
		double h = (double) getHeight();
		if(firstdraw){
			firstdraw=false;
		treemap = new Treemap(model, w, h);
		treemap.squarifynd();
		generateColors();
		}
		if((width!=getWidth())||(height!=getHeight())){
			treemap.setWidth(w);
			treemap.setHeight(w);
			treemap.squarifynd();
			activeneedsupdate=true;

		
			
		}
		NDBounds = treemap.getNdb();
		if(hasrelateditems&&activeneedsupdate){
		updateRelatedItems();}
		Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
		while (nit.hasNext()) {
			
			NewsDeskBoundaries n = nit.next();

           Iterator<ItemBoundaries> iit = n.getItembounds().iterator();
			
			while (iit.hasNext()) {
				ItemBoundaries item = iit.next();
				
				if(activeneedsupdate&&hasactive){
				 if(item.getArt()==active.getArt()){
					 active=item;
					 activeneedsupdate=false;
				 }
				}
				if(sizeview){
			
				g2D.setColor(Color.GRAY);
				g2D.drawRect((int) item.getsizeX(), (int) item.getsizeY(), (int) item.getsizeWidth(), (int) item.getsizeHeight());
				//System.out.println(item.getArt().getHeadline() + ": " + item.getX() + "," + item.getY());
				}
				else{
					
					g2D.setColor(Color.GREEN);
					g2D.drawRect((int) item.getWordx(), (int) item.getWordy(), (int) item.getWordwidth(), (int) item.getWordheight());
					//System.out.println(item.getArt().getHeadline() + ": " + item.getX() + "," + item.getY());
					}
			}
			
			
			if(sizeview){
			//g2D.clearRect((int) n.getSizex(), (int) n.getSizey(), (int) n.getSizewidth(), (int) n.getSizeheight());
			g2D.setColor(colormap.get(n.getNd()));
			g2D.fillRect((int) n.getSizex(), (int) n.getSizey(), (int) n.getSizewidth(), (int) n.getSizeheight());
			g2D.setColor(Color.BLACK);
			g2D.drawString(n.getNd(), (int) n.getSizex(), (int) n.getSizey() + 10);
			g2D.drawRect((int) n.getSizex(), (int) n.getSizey(), (int) n.getSizewidth(), (int) n.getSizeheight());
			}
			else{
				//g2D.clearRect((int) n.getWordx(), (int) n.getWordy(), (int) n.getWordwidth(), (int) n.getWordheight());
				g2D.setColor(colormap.get(n.getNd()));
				g2D.fillRect((int) n.getWordx(), (int) n.getWordy(), (int) n.getWordwidth(), (int) n.getWordheight());
				g2D.setColor(Color.BLACK);
				g2D.drawRect((int) n.getWordx(), (int) n.getWordy(), (int) n.getWordwidth(), (int) n.getWordheight());
				g2D.drawString(n.getNd(), (int) n.getWordx(), (int) n.getWordy() + 10);}

			
		}

		if(hasrelateditems){
			drawRelatedElements(g2D);
		}
		if(hasactive){
			drawActiveElement(g2D);
		}

	}
	
	public void markByKeywords(String key){
		ArrayList<ItemBoundaries> bounds= new ArrayList<ItemBoundaries>();
		Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
		while(nit.hasNext()){
			Iterator<ItemBoundaries> iit = nit.next().getItembounds().iterator();
			while(iit.hasNext()){
				ItemBoundaries item = iit.next();
				Iterator<Keyword> kit = item.getArt().getKeys().iterator();
				while(kit.hasNext()){
					if(kit.next().getValue().equals(key))
					{
						bounds.add(item);
						hasrelateditems=true;
					}
				}
			}
		}
	RelatedBounds= bounds;
	}

	private void updateRelatedItems() {
		Iterator<ItemBoundaries> rit= RelatedBounds.iterator();
		List<ItemBoundaries> newbounds = new ArrayList<ItemBoundaries>();
		Iterator<NewsDeskBoundaries> nit=NDBounds.iterator();
		while(nit.hasNext()){
			Iterator<ItemBoundaries> iit= nit.next().getItembounds().iterator();
			while(iit.hasNext()){
				ItemBoundaries item= iit.next();
				while(rit.hasNext()){
					ItemBoundaries r=rit.next();
					if(item.getArt() ==r.getArt()){
						newbounds.add(item);
					}
				}
			}
		}
		RelatedBounds=newbounds;
	}

	private void drawRelatedElements(Graphics2D g2D) {
		g2D.setColor(Color.RED);
		Iterator<ItemBoundaries> it= RelatedBounds.iterator();
		while(it.hasNext()){
			ItemBoundaries act= it.next();
			if(sizeview){
				g2D.drawRect((int) act.getsizeX(), (int) act.getsizeY(), (int) act.getsizeWidth(), (int) act.getsizeHeight());
			}
			else{
				g2D.drawRect((int) act.getWordx(), (int) act.getWordy(), (int) act.getWordwidth(), (int) act.getWordheight());
			}
		}
	}

	private void drawActiveElement(Graphics2D g2D) {
		g2D.setColor(Color.CYAN);
		if(sizeview){
			g2D.drawRect((int) active.getsizeX(), (int) active.getsizeY(), (int) active.getsizeWidth(), (int) active.getsizeHeight());
		}
		else{
			g2D.drawRect((int) active.getWordx(), (int) active.getWordy(), (int) active.getWordwidth(), (int) active.getWordheight());
		}
		
	}

	private void generateColors() {
		//There can be up to 110 different NewsDesks... no matter the Colors, this will always look weird
		//We go for random colors now and make them transparent so it won't hurt the eye too much
		colormap= new HashMap<String,Color>();
		Iterator<NewsDeskBoundaries> nit = treemap.getNdb().iterator();
		while(nit.hasNext()){
			String key= nit.next().getNd();
			Random rand = new Random(); 
			int r = rand.nextInt(155)+100; 
			int g = rand.nextInt(155)+100; 
			int b = rand.nextInt(155)+100;
			Color value=new Color(r,g,b,70);
			colormap.put(key, value);
		}
		
	}

	private void drawTimelineView(Graphics g) throws ParseException {
		TimelineView t = new TimelineView(model);
		LocalDate start= t.getDatemin();
		LocalDate end = t.getDatemax();
		long diffDays = ChronoUnit.DAYS.between(start, end);
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.clearRect(0, 0, getWidth(), getHeight());
		double middle= (double) getHeight()/2.0;
		int ypos = getHeight()-100;
		System.out.println(diffDays);
		int startx = 20;
		int endx= getWidth()-20;
		double length= (double)endx-(double)startx;
		g2D.drawLine(startx, ypos, endx, ypos);
		//now Add labels for the dates
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for(int i=0; i<=(int)diffDays;i++){
			LocalDate d = start;
			d= start.plusDays(i);
			int x= (endx-startx)/(int)(diffDays);
			x=startx+ i*x;
			
			AffineTransform at = new AffineTransform();
		    at.setToRotation(Math.toRadians(90), x, ypos);
		//    g2D.setTransform(at);
			g2D.drawString(d.format(formatter), x, ypos);	
			at.setToRotation(Math.toRadians(0), x, ypos);
			
		}
		//now give us the data
		Map<String, List<nytvis.timeview.Pair>> data = t.getItems();
		for(String k : data.keySet()){
			List<Pair>plist=data.get(k);
			LocalDate day = start;
			int x1= 0;
			int x2=0;
			int y1=0;
			int y2=0;
			
			for(int i=0; i<=(int)diffDays;i++){
				
				x1= (endx-startx)/(int)(diffDays);
				x1=startx+ i*x1;
				
				//check if there is a data for that day
				int totalval = 0;
				Iterator<Pair> pit= plist.iterator();
				while(pit.hasNext()){
					
					Pair p= pit.next();
					LocalDate d = start;
					d= start.plusDays(i);
					if(p.getDate().equals(d)){
						double scale = (double)p.getValue()/(double)t.getMaxval();
						 y1 = ypos - (int)(scale*(double)(getHeight()-100));
						totalval +=p.getValue();
					}
					else{
						y1= ypos;
					}
				}
				if(totalval>10){
					g2D.setColor(Color.RED);
				}else{
				g2D.setColor(new Color(0,0,0,20));}
				if(i>0){
					g2D.drawLine(x1, y1, x2, y2);
					
					
				}
				x2=x1;
				y2=y1;
				
			}
			
			
		}
		
		
		
		
	}

	public void setModel(Model m) {
		model = m;

	}

	public boolean foundactiveElement(int x, int y) {
		Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
		while (nit.hasNext()) {
			NewsDeskBoundaries nd = nit.next();
			if(sizeview){
				if (x > nd.getSizex() && x < (nd.getSizewidth() + nd.getSizex())) {
					if (y > nd.getSizey() && y < (nd.getSizeheight() + nd.getSizey())) {

						Iterator<ItemBoundaries> it = nd.getItembounds().iterator();
						while (it.hasNext()) {
							ItemBoundaries item = it.next();
							if (x > item.getsizeX() && x < (item.getsizeWidth() + item.getsizeX())) {
								if (y > item.getsizeY() && y < (item.getsizeHeight() + item.getsizeY())) {
									active= item;
									hasactive=true;
									findrelatedArticles();
									return true;
									
								}
							}
						}
					}
				}
			}
			else{
				if (x > nd.getWordx() && x < (nd.getWordwidth() + nd.getWordx())) {
					if (y > nd.getWordy() && y < (nd.getWordheight() + nd.getWordy())) {

						Iterator<ItemBoundaries> it = nd.getItembounds().iterator();
						while (it.hasNext()) {
							ItemBoundaries item = it.next();
							if (x > item.getWordx() && x < (item.getWordwidth() + item.getWordx())) {
								if (y > item.getWordy() && y < (item.getWordheight() + item.getWordy())) {
									active=item;
									hasactive=true;
									findrelatedArticles();
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void setToolText(int x, int y) {
	
		Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
		while (nit.hasNext()) {
			NewsDeskBoundaries nd = nit.next();
			if(sizeview){
				if (x > nd.getSizex() && x < (nd.getSizewidth() + nd.getSizex())) {
					if (y > nd.getSizey() && y < (nd.getSizeheight() + nd.getSizey())) {

						Iterator<ItemBoundaries> it = nd.getItembounds().iterator();
						while (it.hasNext()) {
							ItemBoundaries item = it.next();
							if (x > item.getsizeX() && x < (item.getsizeWidth() + item.getsizeX())) {
								if (y > item.getsizeY() && y < (item.getsizeHeight() + item.getsizeY())) {

									String tiptext = "<html>" + item.getArt().getHeadline() + "<br>";
									tiptext += "Date:" + item.getArt().getPublicationDate() + "<br>";
									tiptext += "Desk:" + item.getArt().getNewsdesk() + "<br>";
									tiptext += "Words:" + item.getArt().getWordCount() + "<br>";
									tiptext += "Keywords:<br>";
									Iterator<Keyword> kit = item.getArt().getKeys().iterator();
									while (kit.hasNext()) {
										Keyword word = kit.next();
										tiptext += word.getValue() + "<br>";

									}

									tiptext += "</html>";

									setToolTipText(tiptext);
								
								}
							}
						}
					}
				}
			}
			else{
				if (x > nd.getWordx() && x < (nd.getWordwidth() + nd.getWordx())) {
					if (y > nd.getWordy() && y < (nd.getWordheight() + nd.getWordy())) {

						Iterator<ItemBoundaries> it = nd.getItembounds().iterator();
						while (it.hasNext()) {
							ItemBoundaries item = it.next();
							if (x > item.getWordx() && x < (item.getWordwidth() + item.getWordx())) {
								if (y > item.getWordy() && y < (item.getWordheight() + item.getWordy())) {

									String tiptext = "<html>" + item.getArt().getHeadline() + "<br>";
									tiptext += "Date:" + item.getArt().getPublicationDate() + "<br>";
									tiptext += "Desk:" + item.getArt().getNewsdesk() + "<br>";
									tiptext += "Words:" + item.getArt().getWordCount() + "<br>";
									tiptext += "Keywords:<br>";
									Iterator<Keyword> kit = item.getArt().getKeys().iterator();
									while (kit.hasNext()) {
										Keyword word = kit.next();
										tiptext += word.getValue() + "<br>";

									}

									tiptext += "</html>";

									setToolTipText(tiptext);
									
								}
							}
						}
					}
				}
			}
		}
	}
	
	

}
