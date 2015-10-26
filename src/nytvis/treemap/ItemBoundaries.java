package nytvis.treemap;

import nytvis.Article;

public class ItemBoundaries {
 private double sizex;
 private double sizey;
 private double sizewidth;
 private double sizeheight;
 private double sizearea;
 private double wordx;
 private double wordy;
 private double wordwidth;
 private double wordheight;
 private double wordarea;
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
public double getWordarea() {
	return wordarea;
}
public void setWordarea(double wordarea) {
	this.wordarea = wordarea;
}
public void setSizearea(double sizearea) {
	this.sizearea = sizearea;
}
public double getWordheight() {
	return wordheight;
}
public void setWordheight(double wordheight) {
	this.wordheight = wordheight;
}
private Article art;
 public double getSizearea() {
	return sizearea;
}
public void setSizeArea(double area) {
	this.sizearea = area;
}

 public ItemBoundaries(Article a, int sx,int sy, int sw, int sh, int wx, int wy, int ww, int wh){
	 art= a;
	 sizex=(double)sx;
	 sizey=(double)sy;
	 sizewidth=(double)sw;
	 sizeheight=(double)sh;
	 wordx=(double) wx;
	 wordy=(double) wy;
	 wordwidth=(double) ww;
	 wordheight=(double) wh;
	 }

public double getsizeX() {
	return sizex;
}
public void setsizeX(double x) {
	this.sizex = x;
}
public double getsizeY() {
	return sizey;
}
public void setsizeY(double y) {
	this.sizey = y;
}
public double getsizeWidth() {
	return sizewidth;
}
public void setsizeWidth(double width) {
	this.sizewidth = width;
}
public double getsizeHeight() {
	return sizeheight;
}
public void setsizeHeight(double height) {
	this.sizeheight = height;
}
public Article getArt() {
	return art;
}
public void setArt(Article art) {
	this.art = art;
}
}
