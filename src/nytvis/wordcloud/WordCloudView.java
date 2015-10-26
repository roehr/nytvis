package nytvis.wordcloud;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.awt.BorderLayout;
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

public class WordCloudView extends JScrollPane{
	private Map<String, Integer> keys=null;
	private Model model;
	private Cloud cloud = null;
	public WordCloudView(){
		
	}
    public void draw() {
    	
    	final JPanel panel = new JPanel();
    	//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    	panel.setLayout(new FlowLayout(FlowLayout.TRAILING,5,5));
       	cloud= new Cloud();
       	cloud.setTagCase(Case.CASE_SENSITIVE);
      
       
        generateKeyWords();
  
        	

        for (Map.Entry<String, Integer> entry : keys.entrySet())
        {
            Tag t = new Tag(entry.getKey(),(double)entry.getValue());
            cloud.addTag(t);
            
        }


        for (Tag tag : cloud.tags()) {
            final JLabel label = new JLabel(tag.getName());
            label.setOpaque(false);
            label.setFont(label.getFont().deriveFont((float) tag.getWeight()*3+10.0f));
            panel.add(label);
        }
        
        
        this.add(panel);
    
         panel.setPreferredSize(new Dimension(200,2000));
         this.setViewportView(panel);
         this.setAutoscrolls(true);
         this.setPreferredSize(new Dimension( 300,700));
        
        
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


	public Model getModel() {
		return model;
	}


	public void setModel(Model model) {
		this.model = model;
	}
}
