package nytvis;

public class Line {
public int x1;
public int x2;
public int y1;
public int y2;
public String text;
Line(String name,int x,int y,int x0, int y0){
	text=name;
	x1=x;
	x2=x0;
	y1=y;
	y2=y0;
}
}
