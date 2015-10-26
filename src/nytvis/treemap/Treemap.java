package nytvis.treemap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nytvis.Article;
import nytvis.Model;

public class Treemap {

	private double width;
	private double height;
	private Model model = null;
	private List<Article> artlist = null;
	private List<List<Article>> sublists = null;
	private List<ItemBoundaries> sizeBounds = null;
	private ItemBoundaries wordBounds = null;
	private List<NewsDeskBoundaries> ndb = null;
	private List<NewsDeskBoundaries> currow = null;
	private int curword;

	public Treemap(Model m, double w, double h) {
		model = m;
		width = w;
		height = h;
		preparemodel();

	}

	private void preparemodel() {
		// subdive the Model by newsdesks and Add them into a Map
		HashSet<String> newsdesks = new HashSet<String>();
		Iterator<Article> artit = model.getElements().iterator();
		// create a List with all Newsdesks...
		while (artit.hasNext()) {
			Article art = artit.next();
			newsdesks.add(art.getNewsdesk());
		}
		sizeBounds = new ArrayList<ItemBoundaries>();
		sublists = new ArrayList<List<Article>>();
		Iterator<String> nit = newsdesks.iterator();
		while (nit.hasNext()) {
			String n = nit.next();
			List<Article> sub = new ArrayList<Article>();
			Iterator<Article> ait = model.getElements().iterator();
			while (ait.hasNext()) {
				Article a = ait.next();
				if ((a.getNewsdesk().equals(n))) {
					sub.add(a);
					ItemBoundaries item = new ItemBoundaries(a,0,0,0,0,0,0,0,0);

				}

			}
			sublists.add(sub);
		}

	}

	public List<ItemBoundaries> getSizeBounds() {
		return sizeBounds;
	}

	public void setSizeBounds(List<ItemBoundaries> sizeBounds) {
		this.sizeBounds = sizeBounds;
	}

	public void squarifynd() {

		List<NewsDeskBoundaries> ndbounds = new ArrayList<NewsDeskBoundaries>();
		int sizetotalcount = 0;
		int wordtotalcount = 0;
		Iterator<List<Article>> it = sublists.iterator();
		while (it.hasNext()) {
			List<Article> artl = it.next();
			List<ItemBoundaries> list = new ArrayList<ItemBoundaries>();
			Iterator<Article> ait = artl.iterator();
			int currwordcount=0;
			while (ait.hasNext()) {
				Article a= ait.next();
				list.add(new ItemBoundaries(a,0,0,0,0,0,0,0,0));
				wordtotalcount += a.getWordCount();
				currwordcount += a.getWordCount();
			}
			sizetotalcount += artl.size();
			NewsDeskBoundaries n = new NewsDeskBoundaries(artl.get(0).getNewsdesk(), 0, 0, 0, 0, 0, 0, 0, 0);
			n.setSizearea(artl.size());
			n.setWordarea(currwordcount);
			n.setItembounds(list);
			n.setWordcount(currwordcount);
			ndbounds.add(n);
		}

		sortbysize(ndbounds);
		double totalarea = (double) width * (double) height;
		double sum = (double) sizetotalcount;
		double multiplier = totalarea / sum;
		Iterator<NewsDeskBoundaries> nit = ndbounds.iterator();
		while (nit.hasNext()) {
			nit.next().multiplySizeArea(multiplier);
		}
		currow = new ArrayList<NewsDeskBoundaries>();
		Rectangle cont = new Rectangle(0, 0, width, height);
		squarifySize(ndbounds, currow, cont);
		ndb = ndbounds;
		
		
		Iterator<NewsDeskBoundaries> ndit = ndb.iterator();
		while (ndit.hasNext()) {
			NewsDeskBoundaries nd = ndit.next();
			curword= nd.getWordcount();
			totalarea = nd.getSizearea();
			Iterator<ItemBoundaries> iit = nd.getItembounds().iterator();
			while (iit.hasNext()) {
				ItemBoundaries item = iit.next();
				item.setSizeArea(totalarea / nd.getItembounds().size());
			}
			List<ItemBoundaries> currow2 = new ArrayList<ItemBoundaries>();
			cont = new Rectangle(nd.getSizex(), nd.getSizey(), nd.getSizewidth(), nd.getSizeheight());
			squarifyartbysize(nd.getItembounds(), currow2, cont);
			
		}
		
		
		
		
		sortbyword(ndbounds);
	    totalarea = (double) width * (double) height;
		sum = (double) wordtotalcount;
		 multiplier = totalarea / sum;
		Iterator<NewsDeskBoundaries> nit2 = ndbounds.iterator();
		while (nit2.hasNext()) {
			nit2.next().multiplyWordArea(multiplier);
		}
		currow = new ArrayList<NewsDeskBoundaries>();
		cont = new Rectangle(0, 0, width, height);
		squarifybyword(ndbounds, currow, cont);
		ndb = ndbounds;
		Iterator<NewsDeskBoundaries> ndit2 = ndb.iterator();
		while (ndit2.hasNext()) {
			NewsDeskBoundaries nd = ndit2.next();
			sortArticlebyWordsize(nd.getItembounds());
			totalarea = nd.getWordarea();
			Iterator<ItemBoundaries> iit = nd.getItembounds().iterator();
			while (iit.hasNext()) {
				ItemBoundaries item = iit.next();
				double wordcountofarticle= (double)item.getArt().getWordCount();
				double wordcountofnewsdesk= (double)nd.getWordcount();
				double wordarea= wordcountofarticle/wordcountofnewsdesk*totalarea;
				item.setWordarea(wordarea);
				//
			}
			List<ItemBoundaries> currow2 = new ArrayList<ItemBoundaries>();
			cont = new Rectangle(nd.getWordx(), nd.getWordy(), nd.getWordwidth(), nd.getWordheight());
			squarifyartbyword(nd.getItembounds(), currow2, cont);
			
		}

	}

	private void sortArticlebyWordsize(List<ItemBoundaries> itembounds) {
		int n = itembounds.size();
		do {
			int newn = 1;
			for (int i = 0; i < n - 1; ++i) {
				if (itembounds.get(i).getArt().getWordCount() < itembounds.get(i+1).getArt().getWordCount()) {
					ItemBoundaries help = itembounds.get(i);
					itembounds.set(i, itembounds.get(i + 1));
					itembounds.set(i + 1, help);
					newn = i + 1;
				}
			}
			n = newn;
		} while (n > 1);
		
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		preparemodel();
	}

	private void sortbyword(List<NewsDeskBoundaries> ndbounds) {
		int n = ndbounds.size();
		do {
			int newn = 1;
			for (int i = 0; i < n - 1; ++i) {
				if (ndbounds.get(i).getWordarea() < ndbounds.get(i + 1).getWordarea()) {
					NewsDeskBoundaries help = ndbounds.get(i);
					ndbounds.set(i, ndbounds.get(i + 1));
					ndbounds.set(i + 1, help);
					newn = i + 1;
				}
			}
			n = newn;
		} while (n > 1);
		
	}

	private boolean squarifyartbyword(List<ItemBoundaries> data, List<ItemBoundaries> currow2, Rectangle cont) {
		if (data.size() == 0) {
			// all data processed, done!
			writeArtWordCoordinates(cont, currow2);
			return true;
		}
		double length = Math.min(cont.height, cont.width);
		ItemBoundaries next = data.get(0);
		if (improveWordart(currow2, next, length)) {
			// improve by adding in current row, add it and move on
			currow2.add(data.get(0));
			squarifyartbyword(data.subList(1, data.size()), currow2, cont);

		} else { // no improve, start new row
			Rectangle newcont = cutArea(cont, sumWordart(currow2));
			writeArtWordCoordinates(cont, currow2);
			
			squarifyartbyword(data, new ArrayList<ItemBoundaries>(), newcont);

		}
		return false;
		
	}

	private boolean improveWordart(List<ItemBoundaries> currow2, ItemBoundaries next, double length) {
		if (currow2.size() == 0)
			return true;

		List<ItemBoundaries> newRow = new ArrayList<ItemBoundaries>(currow2);
		newRow.add(next);

		double curRat = ratioWordart(currow2, length);
		double newRat = ratioWordart(newRow, length);

		return curRat >= newRat;
	}

	private double ratioWordart(List<ItemBoundaries> currow2, double w) {
		double s = 0.0;
		double max = 0.0;

		double min = currow2.get(0).getWordarea();
		Iterator<ItemBoundaries> nit = currow2.iterator();
		while (nit.hasNext()) {
			ItemBoundaries n = nit.next();
			s += n.getWordarea();
			max = Math.max(max, n.getWordarea());
			min = Math.min(min, n.getWordarea());

		}

		return Math.max((w * w * max) / (s * s), (s * s) / (w * w * min));
	}

	private void writeArtWordCoordinates(Rectangle r, List<ItemBoundaries> row) {
		double subxoffset = r.x;
		double subyoffset = r.y;
		double areaWidth = sumWordart(row) / r.height;
		double areaHeight = sumWordart(row) / r.width;

		if (r.width >= r.height) {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setWordx(subxoffset);
				row.get(i).setWordy(subyoffset);
				row.get(i).setWordwidth( areaWidth);
				row.get(i).setWordheight( row.get(i).getWordarea() / areaWidth);
				subyoffset +=row.get(i).getWordheight();
			}
		} else {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setWordx(subxoffset);
				row.get(i).setWordy(subyoffset);
				row.get(i).setWordwidth(row.get(i).getWordarea() / areaHeight);
				row.get(i).setWordheight(areaHeight);
				subxoffset+= row.get(i).getWordwidth();
			}
		}
		
	}

	private double sumWordart(List<ItemBoundaries> row) {
		double res = 0.0;
		Iterator<ItemBoundaries> nit = row.iterator();
		while (nit.hasNext()) {
			res += nit.next().getWordarea();
		}
		return res;
	}

	private boolean squarifybyword(List<NewsDeskBoundaries> data, List<NewsDeskBoundaries> currow2, Rectangle cont) {
		
		if (data.size() == 0) {
			// all data processed, done!
			writeNDWordCoordinates(cont, currow2);
			return true;
		}
		double length = Math.min(cont.height, cont.width);
		NewsDeskBoundaries next = data.get(0);
		if (improveWord(currow2, next, length)) {
			// improve by adding in current row, add it and move on
			currow2.add(data.get(0));
			squarifybyword(data.subList(1, data.size()), currow2, cont);

		} else { // no improve, start new row
			Rectangle newcont = cutArea(cont, sumWordND(currow2));
			writeNDWordCoordinates(cont, currow2);
			squarifybyword(data, new ArrayList<NewsDeskBoundaries>(), newcont);
	

		}
		return false;
	}

	private void writeNDWordCoordinates(Rectangle r, List<NewsDeskBoundaries> row) {
		double subxoffset = r.x;
		double subyoffset = r.y;
		double areaWidth = sumWordND(row) / r.height;
		double areaHeight = sumWordND(row) / r.width;

		if (r.width >= r.height) {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setWordx(subxoffset);
				row.get(i).setWordy(subyoffset);
				row.get(i).setWordwidth( areaWidth);
				row.get(i).setWordheight( row.get(i).getWordarea() / areaWidth);
				subyoffset +=row.get(i).getWordheight();
			}
		} else {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setWordx(subxoffset);
				row.get(i).setWordy(subyoffset);
				row.get(i).setWordwidth(row.get(i).getWordarea() / areaHeight);
				row.get(i).setWordheight(areaHeight);
				subxoffset+= row.get(i).getWordwidth();
			}
		}
		
	}

	private double sumWordND(List<NewsDeskBoundaries> currow2) {
		double res = 0.0;
		Iterator<NewsDeskBoundaries> nit = currow2.iterator();
		while (nit.hasNext()) {
			res += nit.next().getWordarea();
		}
		return res;
	}

	private void sortbysize(List<NewsDeskBoundaries> ndbounds) {
		int n = ndbounds.size();
		do {
			int newn = 1;
			for (int i = 0; i < n - 1; ++i) {
				if (ndbounds.get(i).getSizearea() < ndbounds.get(i + 1).getSizearea()) {
					NewsDeskBoundaries help = ndbounds.get(i);
					ndbounds.set(i, ndbounds.get(i + 1));
					ndbounds.set(i + 1, help);
					newn = i + 1;
				}
			}
			n = newn;
		} while (n > 1);

	}

	public List<NewsDeskBoundaries> getNdb() {
		return ndb;
	}

	public void setNdb(List<NewsDeskBoundaries> ndb) {
		this.ndb = ndb;
	}

	private boolean squarifySize(List<NewsDeskBoundaries> data, List<NewsDeskBoundaries> currow2, Rectangle cont) {
		
		if (data.size() == 0) {
			// all data processed, done!
			writeNDSizeCoordinates(cont, currow2);
			return true;
		}
		double length = Math.min(cont.height, cont.width);
		NewsDeskBoundaries next = data.get(0);
		if (improveSize(currow2, next, length)) {
			// improve by adding in current row, add it and move on
			currow2.add(data.get(0));
			squarifySize(data.subList(1, data.size()), currow2, cont);

		} else { // no improve, start new row
			Rectangle newcont = cutArea(cont, sumSizeND(currow2));
			writeNDSizeCoordinates(cont, currow2);
			squarifySize(data, new ArrayList<NewsDeskBoundaries>(), newcont);
	

		}
		return false;

	}

	private boolean squarifyartbysize(List<ItemBoundaries> data, List<ItemBoundaries> currow2, Rectangle cont) {
		if (data.size() == 0) {
			// all data processed, done!
			writeArtSizeCoordinates(cont, currow2);
			return true;
		}
		double length = Math.min(cont.height, cont.width);
		ItemBoundaries next = data.get(0);
		if (improveSize(currow2, next, length)) {
			// improve by adding in current row, add it and move on
			currow2.add(data.get(0));
			squarifyartbysize(data.subList(1, data.size()), currow2, cont);

		} else { // no improve, start new row
			Rectangle newcont = cutArea(cont, sumSizeart(currow2));
			writeArtSizeCoordinates(cont, currow2);
			
			squarifyartbysize(data, new ArrayList<ItemBoundaries>(), newcont);

		}
		return false;

	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	private boolean improveSize(List<ItemBoundaries> currow2, ItemBoundaries next, double length) {
		if (currow2.size() == 0)
			return true;

		List<ItemBoundaries> newRow = new ArrayList<ItemBoundaries>(currow2);
		newRow.add(next);

		double curRat = ratioSizeart(currow2, length);
		double newRat = ratioSizeart(newRow, length);

		return curRat >= newRat;
	}

	private double ratioSizeart(List<ItemBoundaries> currow2, double w) {
		double s = 0.0;
		double max = 0.0;

		double min = currow2.get(0).getSizearea();
		Iterator<ItemBoundaries> nit = currow2.iterator();
		while (nit.hasNext()) {
			ItemBoundaries n = nit.next();
			s += n.getSizearea();
			max = Math.max(max, n.getSizearea());
			min = Math.min(min, n.getSizearea());

		}

		return Math.max((w * w * max) / (s * s), (s * s) / (w * w * min));
	}

	private static boolean improveWord(List<NewsDeskBoundaries> currRow, NewsDeskBoundaries nextNode, double length) {
		if (currRow.size() == 0)
			return true;

		List<NewsDeskBoundaries> newRow = new ArrayList<NewsDeskBoundaries>(currRow);
		newRow.add(nextNode);

		double curRat = ratioWord(currRow, length);
		double newRat = ratioWord(newRow, length);
		return curRat >= newRat;
	}
	private static boolean improveSize(List<NewsDeskBoundaries> currRow, NewsDeskBoundaries nextNode, double length) {
		if (currRow.size() == 0)
			return true;

		List<NewsDeskBoundaries> newRow = new ArrayList<NewsDeskBoundaries>(currRow);
		newRow.add(nextNode);

		double curRat = ratioSize(currRow, length);
		double newRat = ratioSize(newRow, length);
		return curRat >= newRat;
	}

	private static double ratioSize(List<NewsDeskBoundaries> currRow, double w) {
		double s = 0.0;
		double max = 0.0;
		double min = currRow.get(0).getSizearea();
		Iterator<NewsDeskBoundaries> nit = currRow.iterator();
		while (nit.hasNext()) {
			NewsDeskBoundaries n = nit.next();
			s += n.getSizearea();
			max = Math.max(max, n.getSizearea());
			min = Math.min(min, n.getSizearea());

		}
		double value = Math.max((w * w * max) / (s * s), (s * s) / (w * w * min));
		return value;
	}
	private static double ratioWord(List<NewsDeskBoundaries> currRow, double w) {
		double s = 0.0;
		double max = 0.0;
		double min = currRow.get(0).getWordarea();
		Iterator<NewsDeskBoundaries> nit = currRow.iterator();
		while (nit.hasNext()) {
			NewsDeskBoundaries n = nit.next();
			s += n.getWordarea();
			max = Math.max(max, n.getWordarea());
			min = Math.min(min, n.getWordarea());

		}
		double value = Math.max((w * w * max) / (s * s), (s * s) / (w * w * min));
		return value;
	}

	private Rectangle cutArea(Rectangle cont, double area) {
		Rectangle newCont;
		if (cont.width >= cont.height) {
			double areaWidth = area / cont.height;
			double newWidth = cont.width - areaWidth;
			newCont = new Rectangle(cont.x + areaWidth, cont.y, newWidth, cont.height);
		} else {
			double areaHeight = area / cont.width;
			double newHeight = cont.height - areaHeight;
			newCont = new Rectangle(cont.x, cont.y + areaHeight, cont.width, newHeight);
		
		}
		return newCont;
	}

	private static double sumSizeND(List<NewsDeskBoundaries> currentRow) {
		double res = 0.0;
		Iterator<NewsDeskBoundaries> nit = currentRow.iterator();
		while (nit.hasNext()) {
			res += nit.next().getSizearea();
		}
		return res;
	}

	private static double sumSizeart(List<ItemBoundaries> currentRow) {
		double res = 0.0;
		Iterator<ItemBoundaries> nit = currentRow.iterator();
		while (nit.hasNext()) {
			res += nit.next().getSizearea();
		}
		return res;
	}

	private void writeNDSizeCoordinates(Rectangle r, List<NewsDeskBoundaries> row) {

		double subxoffset = r.x;
		double subyoffset = r.y;
		double areaWidth = sumSizeND(row) / r.height;
		double areaHeight = sumSizeND(row) / r.width;

		if (r.width >= r.height) {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setSizex(subxoffset);
				row.get(i).setSizey(subyoffset);
				row.get(i).setSizewidth( areaWidth);
				row.get(i).setSizeheight( row.get(i).getSizearea() / areaWidth);
				subyoffset +=row.get(i).getSizeheight();
			}
		} else {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setSizex(subxoffset);
				row.get(i).setSizey(subyoffset);
				row.get(i).setSizewidth(row.get(i).getSizearea() / areaHeight);
				row.get(i).setSizeheight(areaHeight);
				subxoffset+= row.get(i).getSizewidth();
			}
		}

	}

	private void writeArtSizeCoordinates(Rectangle r, List<ItemBoundaries> row) {

		double subxoffset = r.x;
		double subyoffset = r.y;
		double areaWidth = sumSizeart(row) / r.height;
		double areaHeight = sumSizeart(row) / r.width;

		if (r.width >= r.height) {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setSizex(subxoffset);
				row.get(i).setSizey(subyoffset);
				row.get(i).setSizewidth( areaWidth);
				row.get(i).setSizeheight( row.get(i).getSizearea() / areaWidth);
				subyoffset +=row.get(i).getSizeheight();
			}
		} else {
			for (int i = 0; i < row.size(); i++) {
				row.get(i).setSizex(subxoffset);
				row.get(i).setSizey(subyoffset);
				row.get(i).setSizewidth(row.get(i).getSizearea() / areaHeight);
				row.get(i).setSizeheight(areaHeight);
				subxoffset+= row.get(i).getSizewidth();
			}
		}
		
	}

}