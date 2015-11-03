package nytvis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import nytvis.wordcloud.WordCloudView;

public class MouseController implements MouseListener, MouseMotionListener {

	private Model model = null;
	private View view = null;
	private WordCloudView wcview = null;
	private int x0 = 0;
	public WordCloudView getWcview() {
		return wcview;
	}


	public void setWcview(WordCloudView wcview) {
		this.wcview = wcview;
	}

	private int y0 = 0;
	private boolean clicked = false;

	public void mouseClicked(MouseEvent arg0) {
		x0 = arg0.getX();
		y0 = arg0.getY();
		if(arg0.getComponent()==view){
			
		if(view.isTimeline()==false){
			
			if(clicked){
			  view.foundactiveElement(x0, y0);
			  clicked=false;
			
	
			}
			else{view.setHasactive(false);
			view.setHasrelateditems(false);
			wcview.getMarked().clear();
			wcview.draw();
			clicked=true;}
			view.repaint();
		}
		else{
			
			view.checkforLinehit(x0, y0);
			wcview.draw();
			 
			view.repaint();
			
		}
		}
		if(arg0.getComponent()==wcview){
		
		
			JScrollPane pane=(JScrollPane)arg0.getComponent();
			int y = pane.getVerticalScrollBar().getValue();
			JLabel active = (JLabel)arg0.getComponent().getComponentAt(x0, y0).getComponentAt(x0, y0).getComponentAt(x0,y0+y);
			wcview.checkKeyList(active.getText());
			wcview.draw();
			view.repaint();
		}
	}
	

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	
	}

	public void mouseReleased(MouseEvent arg0) {
		view.setToolText(arg0.getX(), arg0.getY());
		ToolTipManager.sharedInstance().setDismissDelay(12000);
		ToolTipManager.sharedInstance().registerComponent(view);
	}


	public void mouseDragged(MouseEvent arg0) {


	}

	public void mouseMoved(MouseEvent arg0) {
		view.setToolText(arg0.getX(), arg0.getY());
		ToolTipManager.sharedInstance().setDismissDelay(12000);
		ToolTipManager.sharedInstance().registerComponent(view);
	
	}

	public void setModel(Model model) {
		this.model  = model;	
	}

	public void setView(View view) {
		this.view  = view;
	}

}
