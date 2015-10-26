package nytvis.treemap;

import java.util.List;

import nytvis.Article;

public class NewsDeskBoundaries {
	 private double sizex;
	 private double sizey;
	 private double sizewidth;
	 private double sizeheight;
	 private double sizearea;
	 private double wordarea;
	 private double wordx;
	 private double wordy;
	 private double wordwidth;
	 private double wordheight;
	 private int wordcount;
	 public int getWordcount() {
		return wordcount;
	}
	public void setWordcount(int wordcount) {
		this.wordcount = wordcount;
	}

	private List<ItemBoundaries> itembounds=null;
	 public List<ItemBoundaries> getItembounds() {
		return itembounds;
	}
	public void setItembounds(List<ItemBoundaries> itembounds) {
		this.itembounds = itembounds;
	}
	
	private String nd;
	 public NewsDeskBoundaries(String a, int sx,int sy, int sw, int sh, int wx, int wy, int ww, int wh){
		 nd= a;
		 sizex=(double)sx;
		 sizey=(double)sy;
		 sizewidth=(double)sw;
		 sizeheight=(double)sh;
		 wordx=(double) wx;
		 wordy=(double) wy;
		 wordwidth=(double) ww;
		 wordheight=(double) wh;
		 
	 }
	 
	 public void multiplySizeArea(double a){
		 sizearea*= a;
	 }
	 public void multiplyWordArea(double a){
		 wordarea*= a;
	 }
	public double getSizex() {
		return sizex;
	}
	public void setSizex(double sizex) {
		this.sizex = sizex;
	}
	public double getSizey() {
		return sizey;
	}
	public void setSizey(double sizey) {
		this.sizey = sizey;
	}
	public double getSizewidth() {
		return sizewidth;
	}
	public void setSizewidth(double sizewidth) {
		this.sizewidth = sizewidth;
	}
	public double getSizeheight() {
		return sizeheight;
	}
	public void setSizeheight(double sizeheight) {
		this.sizeheight = sizeheight;
	}
	public double getSizearea() {
		return sizearea;
	}
	public void setSizearea(double sizearea) {
		this.sizearea = sizearea;
	}
	public double getWordarea() {
		return wordarea;
	}
	public void setWordarea(double wordarea) {
		this.wordarea = wordarea;
	}
	public double getWordx() {
		return wordx;
	}
	public void setWordx(double wordx) {
		this.wordx = wordx;
	}
	public double getWordy() {
		return wordy;
	}
	public void setWordy(double wordy) {
		this.wordy = wordy;
	}
	public double getWordwidth() {
		return wordwidth;
	}
	public void setWordwidth(double wordwidth) {
		this.wordwidth = wordwidth;
	}
	public double getWordheight() {
		return wordheight;
	}
	public void setWordheight(double wordheight) {
		this.wordheight = wordheight;
	}
	public String getNd() {
		return nd;
	}
	public void setNd(String nd) {
		this.nd = nd;
	}
}
