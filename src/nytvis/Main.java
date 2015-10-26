package nytvis;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.JSONException;

import javafx.embed.swing.JFXPanel;
import nytvis.gui.GUI;
import nytvis.wordcloud.WordCloudView;


public class Main {
	
//	private MouseController controller = null;
 //   private Model model = null;
    private static View view = null;
    private static WordCloudView wcView= null;
    private MouseController controller= null;
    private KeyController keyController=null;
    public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				
				wcView= new WordCloudView();
				GUI application = new GUI();
				application.setWcView(wcView);
				
				try {
					
					application.getJFrame().setVisible(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

		 public JFXPanel getView() {
				if (view == null) {
					try {
						generate();
					} catch (IOException e) {
						System.out.println("ERROR");
						System.exit(1);
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return view;
			}

		private void generate() throws IOException, InterruptedException, JSONException {
			
			HTTPRequestHandler handler = new HTTPRequestHandler();
			handler.RequestLoop();
			Model model = handler.getModel();
			controller = new MouseController();
			keyController= new KeyController();
			wcView.setModel(model);
			wcView.addMouseListener(controller);
			wcView.draw();
			view = new View();
			view.setWcv(wcView);
			view.setModel(model);
			keyController.setModel(model);
			keyController.setView(view);
			controller.setModel(model);
			controller.setView(view);
			controller.setWcview(wcView);
			view.addMouseListener(controller);
			view.addMouseMotionListener(controller);
			view.addKeyListener(keyController);
			
			

			
		}

		   

}
