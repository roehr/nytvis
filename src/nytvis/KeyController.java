package nytvis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyController implements KeyListener {
	private View view;
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
		if(arg0.getKeyCode() == KeyEvent.VK_D){
			view.removeDeskfromTreemap();
			view.repaint();
		}
		if(arg0.getKeyCode() == KeyEvent.VK_R){
			view.resetTreemap();
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
