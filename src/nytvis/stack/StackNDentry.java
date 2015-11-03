package nytvis.stack;

public class StackNDentry {
private String newsdesk;
private int value=0;
private int y1=0;
private int y2=0;
StackNDentry(String nd, int v){
	newsdesk= nd;
	value=v;
}
public String getNewsdesk() {
	return newsdesk;
}
public void setNewsdesk(String newsdesk) {
	this.newsdesk = newsdesk;
}
public int getValue() {
	return value;
}
public void setValue(int value) {
	this.value = value;
}
public int getY1() {
	return y1;
}
public void setY1(int y1) {
	this.y1 = y1;
}
public int getY2() {
	return y2;
}
public void setY2(int y2) {
	this.y2 = y2;
}


}
