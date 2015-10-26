package nytvis.wordcloud;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Cloud.Case;
import org.mcavallo.opencloud.Tag;

import nytvis.Article;
import nytvis.Keyword;
import nytvis.Model;
import nytvis.View;
import nytvis.treemap.ItemBoundaries;

public class WordCloudView extends JScrollPane{
	private Map<String, Integer> keys=null;
	private Model model;
	private Model backup;
	private Cloud cloud = null;
	private View view = null;
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	private List<String> marked=new ArrayList<String>();
	public WordCloudView(){
		
	}
    public void draw() {
    	
    	final JPanel panel = new JPanel();
    	//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    	panel.setLayout(new FlowLayout(FlowLayout.TRAILING,5,5));
       	cloud= new Cloud();
       	cloud.setTagCase(Case.CASE_SENSITIVE);
       	
      
       
        generateKeyWords();
        cloud.setMaxTagsToDisplay(keys.size());
        	
        for (Map.Entry<String, Integer> entry : keys.entrySet())
        {
            Tag t = new Tag(entry.getKey(),(double)entry.getValue());
            cloud.addTag(t);
            
        }


        for (Tag tag : cloud.tags(new Tag.ScoreComparatorDesc())) {
        	  final JLabel label = new JLabel(tag.getName());
              label.setOpaque(false);
              boolean brushed=false;
        	if(marked.size()>0){
        		Iterator<String> mit=marked.iterator();
        		while(mit.hasNext()){
        			if(mit.next().equals(tag.getName())){
        				label.setForeground(Color.RED);
        				brushed=true;
        				break;
        				
        			}
        		}
        	}
        	
        	if(!brushed){
        		label.setForeground(Color.BLACK);
        	}
           label.setFont(label.getFont().deriveFont((float) tag.getWeight()*3.0f+8.0f));
           panel.add(label);
        }
        
        
        this.add(panel);
    
         panel.setPreferredSize(new Dimension(400,keys.size()*10));
         this.setViewportView(panel);
         this.setAutoscrolls(true);
         this.setPreferredSize(new Dimension( 400,700));
        
        
    }

    public void removeKeys(){
    	Model m= new Model();
    	Iterator<Article> ait= model.getElements().iterator();
    	while(ait.hasNext()){
    		boolean remove=false;
    		Article art=ait.next();
    		Iterator<Keyword> kit= art.getKeys().iterator();
    		while(kit.hasNext()){
    			String key= kit.next().getValue();
    			Iterator<String> mit=marked.iterator();
    				while(mit.hasNext()){
    					if(mit.next().equals(key)){
    						remove=true;
    					}
    				}
    			
    		}
    		if(!remove){
    			
    			m.addArticle(art);
    		}
    	}
    	marked.clear();
    	model=m;
    	view.recalculateArticles(model);
    	generateKeyWords();
    }
    
	public void generateKeyWords() {
		
		Iterator<Article> artit= model.getElements().iterator();
		keys = new HashMap<String,Integer>();
		while(artit.hasNext()){
			Article art=artit.next();
			Iterator<Keyword> kit = art.getKeys().iterator();
			while(kit.hasNext()){
				Keyword k= kit.next();
				if(keys.containsKey(k.getValue())){
				   keys.put(k.getValue(), keys.get(k.getValue()) + 1);
				}
				else{
					keys.put(k.getValue(), 1);
				}
			
			}
			
		}
		
	}
	
	public void checkKeyList(String key){
		//check if element is in List and either remove or add it
		if(marked.size()==0){
			marked.add(key);
		}
		else{
			boolean inside=false;
			Iterator<String> it=marked.iterator();
			while(it.hasNext()){
				if(it.next().equals(key)){
					inside=true;
					it.remove();
				}
			}
			if(!inside){
				marked.add(key);
			}
		}
		if(marked.size()>0){
			view.markByKeywords(marked);
		}
		
	}
	public Model getModel() {
		return model;
	}

	public Model getbackupmodel(){
		return backup;
	}
	public void setModel(Model model) {
		this.model = model;
		this.backup=model;
	}
	public void setmarkedlist(ItemBoundaries active) {
		
		List<String> words= new ArrayList<String>();
		Iterator<Keyword> kit= active.getArt().getKeys().iterator();
		while(kit.hasNext()){
			String word=kit.next().getValue();
			words.add(word);
			
		}
		marked=words;
		draw();
	}
	public void reset(Model model2) {
		model=model2;
		marked.clear();
		generateKeyWords();
		draw();
		
	}
	public void FocusKeys() {
		if(marked.size()==0){
			return;
		}
		System.out.println("HERE");
		Model m= new Model();
    	Iterator<Article> ait= model.getElements().iterator();
    	while(ait.hasNext()){
    		Article art=ait.next();
    		Iterator<Keyword>kit=art.getKeys().iterator();
    		while(kit.hasNext()){
    			Keyword k= kit.next();
    			Iterator<String> mit=marked.iterator();
    			while(mit.hasNext()){
    				
    				if(mit.next().equals(k.getValue())){
    					m.addArticle(art);
    					
    				}
    			}
    		}
    		
    	}
    	model=m;
    	draw();
    	view.recalculateArticles(model);
	}
}
