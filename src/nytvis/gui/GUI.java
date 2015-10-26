
package nytvis.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.json.JSONException;

import javafx.embed.swing.JFXPanel;
import nytvis.Main;
import nytvis.wordcloud.WordCloudView;


/**
 *
 * @author JayDee
 */



public class GUI  {
		private JFrame jFrame = null;
	    private JSplitPane jContentPane= null;
	    
	    private JFXPanel view= null;
	    private JScrollPane  wcview = null;
	 
	    private JPanel  jWCPanel = null;
		public JFrame getJFrame() throws IOException, InterruptedException, JSONException {
		
			if (jFrame == null) {
				jFrame = new JFrame();
				jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));				
				jFrame.setContentPane(getJContentPane());
				jFrame.setSize(1024, 768);
				jFrame.setTitle("NYVis");
			
				
			}
			return jFrame;
		}
		
		private JSplitPane getJContentPane() throws IOException, InterruptedException, JSONException {
			if (jContentPane == null) {
				jContentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getView(), getWordCloudPanel());
			//	jContentPane.setLayout(new BorderLayout(10,10));
			//    jContentPane.add(getView(), BorderLayout.CENTER);
			//    jContentPane.add(getWordCloudPanel(), BorderLayout.EAST);
				getView().setMinimumSize(new Dimension(800,600));
				getWordCloudPanel().setMaximumSize(new Dimension(300,3000));
			    
			}
			return jContentPane;
		}




		
		
		private JPanel getWordCloudPanel() {
			if (jWCPanel == null) {
				jWCPanel = new JPanel();
				jWCPanel.add(getWcView());
				
			}
			return jWCPanel;
		}

		
		
		
		
		private JScrollPane getWcView() {
			if (wcview == null) {
				wcview = new WordCloudView();
				
			}
			return wcview;
		
		}
		public void setWcView(JScrollPane wc){
			this.wcview=wc;
		}

		private JFXPanel getView() throws IOException, InterruptedException, JSONException {
			if (view == null) {
				view = new Main().getView();
				
			}
			return view;
		}


                     
}
