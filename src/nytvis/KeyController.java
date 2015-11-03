package nytvis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import nytvis.wordcloud.WordCloudView;

public class KeyController implements KeyListener {
	private View view;
	private WordCloudView wcview;
	public WordCloudView getWcview() {
		return wcview;
	}

	public void setWcview(WordCloudView wcview) {
		this.wcview = wcview;
	}

	private Model model;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_M){
			if(view.isSizeview()){
			view.setSizeview(false);}
			else{view.setSizeview(true);}
			view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_N){
			if(view.isStackview()){
				view.setStackview(false);
			}
			else{view.setStackview(true);
			   }
			view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_V){
			if(view.isTimeline()){
				view.setTimeline(false);
			}
			else{
				view.setTimeline(true);
			}
			view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_D&&view.isHasactive()){
			view.removeDeskfromTreemap();
			view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_F&&view.isHasactive()){
			if(!view.isTimeline()){
			view.FocusDesk();
			view.repaint();}
			else{
				wcview.FocusKeys();
				if(view.isHasrelateditems()){
					view.setHasrelateditems(false);
				}
				view.recalculateArticles(wcview.getModel());
		    	view.repaint();
		    	wcview.draw();
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_F&&!view.isHasactive()){
			wcview.FocusKeys();
			if(view.isHasrelateditems()){
				view.setHasrelateditems(false);
			}
			view.recalculateArticles(wcview.getModel());
	    	view.repaint();
	    	wcview.draw();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_D&&!view.isHasactive()){
			wcview.removeKeys();
			wcview.draw();
			view.recalculateArticles(wcview.getModel());
	    	view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_R){
			view.resetTreemap();
			view.repaint();
			
		}
		if(arg0.getKeyCode() == KeyEvent.VK_H){
			if(view.isHidecolors()){
				view.setHidecolors(false);
			}
			else{view.setHidecolors(true);}
			view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_C){
			view.generateColors();
			view.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
