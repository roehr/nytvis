package nytvis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.ToolTipManager;

import javafx.embed.swing.JFXPanel;
import nytvis.stack.Stack;
import nytvis.stack.StackNDentry;
import nytvis.stack.Stackitem;
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
	private boolean stackview = false;
	private static final long serialVersionUID = 1L;
	private Stack s;
	private Model model = null;
	private Treemap treemap=null;
	private List<Line> timelines = null;
	private List<NewsDeskBoundaries> NDBounds = null;
	private boolean sizeview = false;
	private boolean timeline = false;
	private ItemBoundaries active = null;
	private boolean hasactive = false;
	private boolean activeneedsupdate = false;
	private boolean hidecolors=false;
	private List<ItemBoundaries> RelatedBounds = new ArrayList<ItemBoundaries>();
	private boolean hasrelateditems = false;
	private boolean relatedneedsupdate= false;
	boolean firstdraw=true;
	HashMap<String, Color> colormap = null;
	private int width;
	private List<Line> markedlines = new ArrayList<Line>();
	private int height;
	boolean stackupdate=true;
	List<Pair> brusheditem=new ArrayList<Pair>();
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
		if (timeline) {
			if(stackview){
				drawStackview(g);
			}
			else{
			try {
				ToolTipManager.sharedInstance().setEnabled(false);
				drawTimelineView(g);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		} else {
	
			drawTreemap(g);
			
		}

	}

	public boolean isStackview() {
		return stackview;
	}

	public void setStackview(boolean stackview) {
		this.stackview = stackview;
	}

	public List<Line> getTimelines() {
		return timelines;
	}

	public void setTimelines(List<Line> timelines) {
		this.timelines = timelines;
	}

	public boolean isTimeline() {
		return timeline;
	}

	public void setTimeline(boolean timeline) {
		this.timeline = timeline;
	}

	private void findrelatedArticles(){
		if(hasactive){

			List<ItemBoundaries> bounds = new ArrayList<ItemBoundaries>();
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
									bounds.add(item);
									hasrelateditems=true;
								}
							}
						}
					}
				}
			}
			RelatedBounds= bounds;
		
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
	
	public void FocusDesk(){
		if(hasactive){

			Model m= new Model();
			Iterator<Article>mit =model.getElements().iterator();
			while(mit.hasNext()){
				Article art=mit.next();
				if(art.getNewsdesk().equals(active.getArt().getNewsdesk())){
					m.addArticle(art);
				}
			}
			treemap.setModel(m);
			treemap.squarifynd();
			NDBounds=treemap.getNdb();
			wcv.setModel(m);
			wcv.generateKeyWords();
			wcv.draw();
			recalculatemarkedArticles();
		}
		
	}
	public void setHasactive(boolean hasactive) {
		this.hasactive = hasactive;
	}
	public void resetTreemap(){
		treemap.setModel(model);
		treemap.squarifynd();
		wcv.reset(model);
		hasactive=false;
		hasrelateditems=false;
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
			NDBounds=treemap.getNdb();
			hasrelateditems=false;
			wcv.setModel(treemapmodel);
			wcv.generateKeyWords();
			wcv.draw();
			repaint();
		}
	
	}
	private void recalculatemarkedArticles() {
		//recheck if all related Articles still exist
		Iterator<ItemBoundaries>iit=RelatedBounds.iterator();
		List<ItemBoundaries> newrels= new ArrayList<ItemBoundaries>();
		while(iit.hasNext()){
			ItemBoundaries activemark= iit.next();
			Iterator<NewsDeskBoundaries>nit = NDBounds.iterator();
			while(nit.hasNext()){
				NewsDeskBoundaries nd= nit.next();
				if(activemark.getArt().getNewsdesk().equals(nd.getNd())){
					Iterator<ItemBoundaries> ndiit= nd.getItembounds().iterator();
					while(ndiit.hasNext()){
						ItemBoundaries nditem =ndiit.next();
						if(nditem.getArt().equals(activemark.getArt())){
							newrels.add(nditem);
						}
					}
				}
			}
		}
		RelatedBounds=newrels;
		relatedneedsupdate=true;
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
			relatedneedsupdate=true;
			
		}
		NDBounds = treemap.getNdb();
		if(hasrelateditems&&relatedneedsupdate){
		updateRelatedItems();
		relatedneedsupdate=false;}
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
				g2D.drawRect((int) item.getSizex(), (int) item.getSizey(), (int) item.getSizewidth(), (int) item.getSizeheight());
				//System.out.println(item.getArt().getHeadline() + ": " + item.getX() + "," + item.getY());
				}
				else{
					
					g2D.setColor(Color.GRAY);
					g2D.drawRect((int) item.getWordx(), (int) item.getWordy(), (int) item.getWordwidth(), (int) item.getWordheight());
					//System.out.println(item.getArt().getHeadline() + ": " + item.getX() + "," + item.getY());
					}
			}
			
			
			if(sizeview){
			//g2D.clearRect((int) n.getSizex(), (int) n.getSizey(), (int) n.getSizewidth(), (int) n.getSizeheight());
			if(hidecolors==false){
				g2D.setColor(colormap.get(n.getNd()));}
			else{g2D.setColor(new Color(255,255,255,30));}
			g2D.fillRect((int) n.getSizex(), (int) n.getSizey(), (int) n.getSizewidth(), (int) n.getSizeheight());
			g2D.setColor(Color.BLACK);
			g2D.drawString(n.getNd(), (int) n.getSizex(), (int) n.getSizey() + 10);
			g2D.drawRect((int) n.getSizex(), (int) n.getSizey(), (int) n.getSizewidth(), (int) n.getSizeheight());
			}
			else{
				//g2D.clearRect((int) n.getWordx(), (int) n.getWordy(), (int) n.getWordwidth(), (int) n.getWordheight());
				if(hidecolors==false){
					g2D.setColor(colormap.get(n.getNd()));}
				else{g2D.setColor(new Color(255,255,255,30));}
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
	
	public boolean isHidecolors() {
		return hidecolors;
	}

	public void setHidecolors(boolean hidecolors) {
		this.hidecolors = hidecolors;
	}

	public boolean isHasrelateditems() {
		return hasrelateditems;
	}

	public void setHasrelateditems(boolean hasrelateditems) {
		this.hasrelateditems = hasrelateditems;
	}

	public void markByKeywords(List<String> keys){
		hasactive=false;
		ArrayList<ItemBoundaries> bounds= new ArrayList<ItemBoundaries>();
		Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
		while(nit.hasNext()){
			Iterator<ItemBoundaries> iit = nit.next().getItembounds().iterator();
			while(iit.hasNext()){
				ItemBoundaries item = iit.next();
				Iterator<Keyword> kit = item.getArt().getKeys().iterator();
				while(kit.hasNext()){
					String key=kit.next().getValue();
					Iterator<String> it=keys.iterator();
					while(it.hasNext()){
						if(key.equals(it.next()))
						{
							bounds.add(item);
							hasrelateditems=true;
						}
					}
				}
			}
		}
	
	RelatedBounds= bounds;
	}

	
	private void updateRelatedItems() {
		for(int i=0;i<RelatedBounds.size(); i++){
			Article art= RelatedBounds.get(i).getArt();
			Iterator<NewsDeskBoundaries> nit= NDBounds.iterator();
			boolean found = false;
			while(nit.hasNext()&&!found){
				Iterator<ItemBoundaries>iit= nit.next().getItembounds().iterator();
				while(iit.hasNext()&&!found){
					ItemBoundaries  item = iit.next();
					if(item.getArt().equals(art)){
						found=true;
						RelatedBounds.get(i).setVars(item);
					}
					
				}
			}
			if(!found){
				RelatedBounds.remove(i);
			}
			
			
		}
	}

	private void drawRelatedElements(Graphics2D g2D) {
		
	
		if(!hasactive){
			Iterator<ItemBoundaries> it= RelatedBounds.iterator();
			while(it.hasNext()){
				ItemBoundaries act= it.next();
				if(sizeview){
					g2D.setColor(new Color(0,0,0,20));
					g2D.fillRect((int) act.getSizex(), (int) act.getSizey(), (int) act.getSizewidth(), (int) act.getSizeheight());
					g2D.setColor(Color.BLACK);
					g2D.drawRect((int) act.getSizex(), (int) act.getSizey(), (int) act.getSizewidth(), (int) act.getSizeheight());
				}
				else{
					g2D.setColor(new Color(0,0,0,20));
					g2D.fillRect((int) act.getWordx(), (int) act.getWordy(), (int) act.getWordwidth(), (int) act.getWordheight());
					g2D.setColor(Color.BLACK);
					g2D.drawRect((int) act.getWordx(), (int) act.getWordy(), (int) act.getWordwidth(), (int) act.getWordheight());
				}
			}
		}
		else{
			Iterator<ItemBoundaries> it= RelatedBounds.iterator();
			while(it.hasNext()){
			
					ItemBoundaries obj= it.next();
					int counter = 0;
					Iterator<Keyword> keywordsactive = active.getArt().getKeys().iterator();
					while(keywordsactive.hasNext()){
						Keyword act= keywordsactive.next();
						Iterator<Keyword> keywordsrelated = active.getArt().getKeys().iterator();
						while(keywordsrelated.hasNext()){
							if(act.getValue().equals(keywordsrelated.next().getValue())){
								counter++;
							}
						}
					}
					double alpha= (double)counter/(double)active.getArt().getKeys().size()*100;
					g2D.setColor(new Color(150,150,150,(int)alpha));
					if(sizeview){
						g2D.fillRect((int) obj.getSizex(), (int) obj.getSizey(), (int) obj.getSizewidth(), (int) obj.getSizeheight());
						g2D.setColor(Color.BLACK);
						g2D.drawRect((int) obj.getSizex(), (int) obj.getSizey(), (int) obj.getSizewidth(), (int) obj.getSizeheight());
						
					}
					else{
						g2D.fillRect((int) obj.getWordx(), (int) obj.getWordy(), (int) obj.getWordwidth(), (int) obj.getWordheight());
						g2D.setColor(Color.BLACK);
						g2D.drawRect((int) obj.getWordx(), (int) obj.getWordy(), (int) obj.getWordwidth(), (int) obj.getWordheight());
					}
				
			}
			
			//Labels need a redraw now - if there are many related articles, you won't see them anymore...
			Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
			while(nit.hasNext()){
				NewsDeskBoundaries n = nit.next();
				if(sizeview){
					g2D.setColor(Color.BLACK);
					g2D.drawString(n.getNd(), (int) n.getSizex(), (int) n.getSizey() + 10);
					}
					else{
			
						g2D.setColor(Color.BLACK);
						g2D.drawString(n.getNd(), (int) n.getWordx(), (int) n.getWordy() + 10);}

					
				}
			}
		}
	
	

	private void drawActiveElement(Graphics2D g2D) {
		g2D.setColor(Color.RED);
		if(sizeview){
			g2D.drawRect((int) active.getSizex(), (int) active.getSizey(), (int) active.getSizewidth(), (int) active.getSizeheight());
		}
		else{
			g2D.drawRect((int) active.getWordx(), (int) active.getWordy(), (int) active.getWordwidth(), (int) active.getWordheight());
		}
		
	}

	public void generateColors() {
		//There can be up to 110 different NewsDesks... no matter the Colors, this will always look weird
		//We go for random colors now and make them transparent so it won't hurt the eye too much
		//Bad solution still - this is asking for bad results...
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
	private void drawStackview(Graphics g){
		int ypos = getHeight() - 100;
		int startx = 20;
		int width = getWidth() - 30;
		int height=getHeight()-150;
		//stackview only uses marked items
		
			Model m = new Model();
			Iterator<ItemBoundaries> iit = RelatedBounds.iterator();
			
			while( iit.hasNext()){
				Article a=iit.next().getArt();
				Iterator<Article> ait= m.getElements().iterator();
				boolean found = false;
				while(ait.hasNext()){
					if(ait.next().getHeadline().equals(a.getHeadline())){
						found=true;
						break;
					}
				}
				if(found==false){
					m.addArticle(a);
				}
			}
		    s=new Stack(m,wcv,startx,ypos,width,height);
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.clearRect(0, 0, getWidth(), getHeight());
		for(int i= 0; i<s.getMaxvalue();i++){
			if(i%2==0){
			g2D.drawString(Integer.toString(i), s.getStartx(), s.getStarty()-i*s.getHeight()/s.getMaxvalue());}
		}
		int i=0;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		
		for(LocalDate k : s.getStack().keySet()){
			g2D.setColor(Color.BLACK);
			g2D.drawString(k.format(formatter), s.getStartx()+i*s.getWidth()/s.getStack().size(), s.getStarty()+10);
			i++;
			Iterator<Stackitem> stit=s.getStack().get(k).iterator();
			while(stit.hasNext()){
				Stackitem sitem= stit.next();
				g2D.setColor(Color.BLACK);
				//check if keyword needs to be marked
				Iterator<String> mit= wcv.getMarked().iterator();
				while(mit.hasNext()){
					if(mit.next().equals(sitem.getKeyword())){
						g2D.setColor(Color.RED);
						break;
					}
				}
				
				g2D.drawRect(sitem.getX1(), sitem.getY2(), sitem.getX2()-sitem.getX1(),sitem.getY1()-sitem.getY2());
				for(String nd: sitem.getValuesperDesk().keySet()){
					g2D.setColor(colormap.get(nd));
					g2D.fillRect(sitem.getX1(), sitem.getValuesperDesk().get(nd).getY2(), sitem.getX2()-sitem.getX1(), sitem.getValuesperDesk().get(nd).getY1()-sitem.getValuesperDesk().get(nd).getY2());
					g2D.setColor(new Color(0,0,0,20));
					g2D.drawRect(sitem.getX1(), sitem.getValuesperDesk().get(nd).getY2(), sitem.getX2()-sitem.getX1(), sitem.getValuesperDesk().get(nd).getY1()-sitem.getValuesperDesk().get(nd).getY2());
					
				}
			}
		}
		
		
		
	}
	private void drawTimelineView(Graphics g) throws ParseException {
		TimelineView t = new TimelineView(treemap.getModel());
		LocalDate start = t.getDatemin();
		LocalDate end = t.getDatemax();
		long diffDays = ChronoUnit.DAYS.between(start, end);

		Graphics2D g2D = (Graphics2D) g;
		g2D.clearRect(0, 0, getWidth(), getHeight());
		int ypos = getHeight() - 100;
		int startx = 20;
		int endx = getWidth() - 20;
		double length = (double) endx - (double) startx;
		g2D.drawLine(startx, ypos, endx, ypos);
		// now Add labels for the dates
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		for (int i = 0; i <= (int) diffDays; i++) {
			LocalDate d = start;
			d = start.plusDays(i);
			int x=0;
		    if(diffDays>0.0){
			x = (endx - startx) / (int) (diffDays);}
		   
			x = startx + i * x;

			g2D.drawString(d.format(formatter), x, ypos+12);

		}
		//y-axis:
		g2D.drawLine(startx, ypos, startx, (getHeight()-150));
		for(int i = 1; i<=t.getMaxval();i++){
			double scale = (double)(getHeight()-150.0)*i / (double) t.getMaxval();
			int y=ypos-(int)scale;
			g2D.drawString(Integer.toString(i), startx, y);
		}
		
		timelines=new ArrayList<Line>();
		Map<String, List<nytvis.timeview.Pair>> data = t.getItems();
		
		for (String k : data.keySet()) {
			List<Pair> plist = data.get(k);
			LocalDate day = start;
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			boolean brushed = false;
			for (int i = 0; i <= (int) diffDays; i++) {
				x1=0;
				if((int)diffDays>0){
				x1 = (endx - startx) / (int) (diffDays);}
				x1 = startx + i * x1;
				// check for data
				boolean found=false;
				Iterator<Pair> pit = plist.iterator();
				while (pit.hasNext()) {
					Pair p = pit.next();
					//check for Keywordmatch
					if(hasrelateditems){
					
						Iterator<String> kit =wcv.getMarked().iterator();
						while(kit.hasNext()){
							if(kit.next().equals(k)){
								brushed=true;
							}
						}
					
					}
					
					LocalDate d = start;
					d = start.plusDays(i);
					if (p.getDate().equals(d)) {
						double scale = (double)(getHeight()-150.0)*(double) p.getValue() / (double) t.getMaxval();
						
					
						y1=ypos-(int)scale;
						found=true;

					} 

				}
				if(!found){
					y1=ypos;
				}

				if (i > 0) {
					g2D.setColor(new Color(0,0,0,20));
					g2D.drawLine(x1, y1, x2, y2);
					Line l = new Line(k,x1, y1, x2, y2);
					timelines.add(l);
					
					if (brushed) {
					
						markedlines.add(l);
					
					}

				}

				x2 = x1;
				y2 = y1;
				
			}

		}
		Iterator<Line> mit=markedlines.iterator();
		List<Line> lines = new ArrayList<Line>();
		while(mit.hasNext()){
			String name= mit.next().text;
			Iterator<Line> tit=timelines.iterator();
			wcv.checkKeyList(name);
			while(tit.hasNext()){
				Line currline= tit.next();
				if(currline.text.equals(name)){
					lines.add(currline);
				}
			}
		}
		markedlines=lines;
	
	
		Iterator<Line>mit2=markedlines.iterator();
		while(mit2.hasNext()){
			Line l=mit2.next();
			g2D.setColor(new Color(255,0,0,20));
			
			g2D.drawLine(l.x1, l.y1, l.x2, l.y2);
		}
		markedlines.clear();
	}

	public void checkforLinehit(int x, int y){

		
		Iterator<Line> lit= timelines.iterator();
		while(lit.hasNext()){
			Line line=lit.next();
			if(intersect(x,y,line)){
				wcv.checkKeyList(line.text);
				
			}
			
			
		}
		
		
	}

	private boolean intersect(int xin, int yin, Line line) {
		float minX= (float)xin;
		float maxX= (float)xin+4.0f;
		float minY = (float)yin;
		float maxY= (float)yin+4;
		float x1= (float)line.x1;
		float x2=(float)line.x2;
		float y1=(float)line.y1;
		float y2=(float)line.y2;
		    if ((x1 <= minX && x2 <= minX) || (y1 <= minY && y2 <= minY) || (x1 >= maxX && x2 >= maxX) || (y1 >= maxY && y2 >= maxY))
		        return false;
		    float m = (y2 - y1) / (x2 - x1);
		    float y = m * (minX - x1) + y1;
		    if (y > minY && y < maxY) return true;
		    y = m * (maxX - x1) + y1;
		    if (y > minY && y < maxY) return true;
		    float x = (minY - y1) / m + x1;
		    if (x > minX && x < maxX) return true;
		    x = (maxY - y1) / m + x1;
		    if (x > minX && x < maxX) return true;
		    return false;
		
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
							if (x > item.getSizex() && x < (item.getSizewidth() + item.getSizex())) {
								if (y > item.getSizey() && y < (item.getSizeheight() + item.getSizey())) {
									active= item;
									hasactive=true;
								
									wcv.setmarkedlist(item);
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
									wcv.setmarkedlist(item);
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
	if(!timeline){

		Iterator<NewsDeskBoundaries> nit = NDBounds.iterator();
		while (nit.hasNext()) {
			NewsDeskBoundaries nd = nit.next();
			if(sizeview){
				if (x > nd.getSizex() && x < (nd.getSizewidth() + nd.getSizex())) {
					if (y > nd.getSizey() && y < (nd.getSizeheight() + nd.getSizey())) {

						Iterator<ItemBoundaries> it = nd.getItembounds().iterator();
						while (it.hasNext()) {
							ItemBoundaries item = it.next();
							if (x > item.getSizex() && x < (item.getSizewidth() + item.getSizex())) {
								if (y > item.getSizey() && y < (item.getSizeheight() + item.getSizey())) {

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
									ToolTipManager.sharedInstance().setEnabled(true);
									return;
								
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
									ToolTipManager.sharedInstance().setEnabled(true);
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	else{
		 if(stackview&&timeline){
		
				{ 	
					if (x>s.getStartx()&&x<s.getWidth()+s.getStartx()){
					
						if(y <s.getStarty()&&y>s.getStarty()-s.getHeight()){
							for(LocalDate d : s.getStack().keySet()){
								Iterator<Stackitem> sit = s.getStack().get(d).iterator();
								while(sit.hasNext()){
									Stackitem si= sit.next();
									if(si.getX1()<x &&si.getX2()>x){
									
									  if(y<si.getY1()&&y>si.getY2()){
										
											for(String nd : si.getValuesperDesk().keySet()){
												StackNDentry ndentry=si.getValuesperDesk().get(nd);
												
												if(y>ndentry.getY2()&&y<ndentry.getY1()){
												
													String tiptext = "<html>" +ndentry.getNewsdesk() + "<br>";
													tiptext += "Keyword"+si.getKeyword() + "<br>";
													tiptext += "Appearance:" +ndentry.getValue() + "<br>";
													tiptext += "</html>";

													setToolTipText(tiptext);
													ToolTipManager.sharedInstance().setEnabled(true);
													return;
												}
												
											}
									  }
									}
								}
							}
						}
					}
					
				}
			}
	}
	ToolTipManager.sharedInstance().setEnabled(false);
	}
	
	public void setstacktooltext(int x, int y){

	}

	public void recalculateArticles(Model model2) {
		treemap= new Treemap(model2,(double)getWidth(),(double)getHeight());
		recalculatemarkedArticles();
		treemap.squarifynd();
		NDBounds=treemap.getNdb();
		repaint();
	}
	
	

}
