package nytvis.timeview;

import java.time.LocalDate;
import java.util.Date;

import nytvis.Keyword;

public class Pair {
 private LocalDate date;
 private int value;
 public Pair(LocalDate d, int val){
	 date=d;
	 value=val;
 }
public LocalDate getDate() {
	return date;
}
public void setDate(LocalDate d) {
	this.date = d;
}
public int getValue() {
	return value;
}
public void setValue(int value) {
	this.value = value;
}

public void print(){
	System.out.println(date + ": " + value);
}
}
