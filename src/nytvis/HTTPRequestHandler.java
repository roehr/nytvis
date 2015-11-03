package nytvis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;


public class HTTPRequestHandler {
	private String apikeyart = "6b4d22e3c89d5f8aca861dbd6850a07a%3A14%3A72956643";
	private String baseurl = "http://api.nytimes.com/svc/search/v2/articlesearch.json?";
	private String begin = "20010909";
	private String end = "20010920";
	private String query = "";
	private Model model;
	private int ERRcount=0;
	private int page = 0;
	JSONObject obj = new JSONObject();
	private BufferedReader in;

	public void generateQuery() {
		query = "&begin_date=" + begin + "&end_date=" + end + "&sort=oldest" +"&page=" + page + "&api-key=" + apikeyart;
	}
	
	public void RequestLoop() throws IOException, InterruptedException, JSONException{
	
		System.out.println("APIRequest");
		model= new Model();
		while(begin.equals(end)==false){
				for( page= 1; page<=5;page++){
					//we read up to 50 articles per day this way
					//Note, that this could easily made accurate using the JSONResponse "hits" query - not used here due to the Requestlimit of the API though!
					
					generateQuery();
					MakeRequest();
					}
				addDay();
			
		}
		System.out.println("Done. Received " + model.getElements().size() + " Elements. There were " + ERRcount + " unreadable Articles." );
		System.out.println("Total Words: " + model.getTotalWords());
		model.NewsDeskListing();
	}

	public void MakeRequest() throws IOException, InterruptedException, JSONException {
		

			//generate the Query
			generateQuery();
			
			//Set Requesturl
			URL obj = new URL(baseurl + query);
			//Connect
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Read the JSON
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			// Response is the JSON form of type StringBuffer
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				// jstr is the string version of the JSON form
				String jstr = response.toString();
                handleJSON(jstr);				
			}
			in.close();

		
	}

	private void handleJSON(String jstr) throws JSONException {
		JSONObject jsonObj = new JSONObject(jstr);
		JSONArray arr = jsonObj.getJSONObject("response").getJSONArray("docs");
		Article art;
		// Loop through the docs array
		int i = 0;
		while (i < arr.length()) {
			boolean success = true;
			art = new Article();
			try {
				art.setHeadline(arr.getJSONObject(i).getJSONObject("headline").getString("main"));

				art.setPublicationDate(arr.getJSONObject(i).getString("pub_date"));
				

				art.setWordCount(arr.getJSONObject(i).getInt("word_count"));
				art.setNewsdesk(arr.getJSONObject(i).getString("news_desk"));
				int j = 0;
				while(j<arr.getJSONObject(i).getJSONArray("keywords").length()){
					
					String name= arr.getJSONObject(i).getJSONArray("keywords").getJSONObject(j).getString("name");
					String value =arr.getJSONObject(i).getJSONArray("keywords").getJSONObject(j).getString("value");
					art.addKey(name, value);
					j++;
				}
			
			} catch (JSONException e) {
				success = false;
				ERRcount++;
				i++;
				//e.printStackTrace();
				System.out.println("Error with this JSONResponse. Skipping Article...");
				continue;
			}
			if (success) {
				model.addArticle(art);
			}
			i++;
		}
		
	}

	private void addDay() {

		int year = Integer.valueOf(begin.substring(0, 4));
		int month = Integer.valueOf(begin.substring(4, 6));
		int day = Integer.valueOf(begin.substring(6, 8));
		boolean help = false;
		
		
		if (month == 2) {
			// check 28th of the month:
			if (day == 28) {
				if ((year % 4 == 0) && (year % 100 == 0) && (year % 400 == 0)) {
					day++;
				} else {
					day = 1;
					month = 3;
					help=true;
				}
				if (day == 29) {
					day = 1;
					month = 3;
					help=true;

				}
			}
		}
		if ((month == 1) || (month == 3) || (month == 5) || (month == 7) || (month == 8) || (month == 10)) {
			if (day == 31) {
				day = 1;
				month++;
				help=true;
			}

		}
		if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
			if (day == 30) {
				day = 1;
				month++;
				help=true;
			}
		}

		if ((month == 12) && (day == 31)) {
			year++;
			day = 1;
			month = 1;
			help=true;
		} 
		if(!help){
			day++;
		}
		String res = "";
		res += String.format("%04d", year);
		res += String.format("%02d", month);
		res += String.format("%02d", day);
		begin = res;
		

	}

	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Model getModel() {
		if(model!= null){
			return model;
		}
		return null;
	}

}
