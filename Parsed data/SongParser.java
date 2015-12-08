import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;







import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SongParser {

	HashMap<String, String> songListMap = new HashMap<>();
	static HashMap<String,SongData> index = new HashMap<String, SongData>();
	
	String filePath = "C:\\Users\\Aniruddha Kakade\\Desktop\\Academics\\IR\\Project\\RawDataGaana\\";
	
	public void putData(JSONObject obj, String url, String pageTitle,String meta ) throws JSONException{
		
		String songId = obj.getString("id");
		String title= obj.getString("title");
		String album = obj.getString("albumtitle");
		String artist = obj.getString("artist");
		String albumart = obj.getString("albumartwork");
		
		artist = artist.substring(0, artist.indexOf("#"));
		if(album.isEmpty()) album = "*^*";
		if(title.isEmpty()) title = "*^*";
		if(artist.isEmpty()) artist = "*^*";
		if(albumart.isEmpty()) albumart = "*^*";
				//System.out.println(obj);
				if(!index.containsKey(songId)){
					index.put(songId, new SongData(title, url, album, artist, "*^*", "*^*", albumart,pageTitle,meta));
				}
	}
	
	
	public void parseData() throws FileNotFoundException, JSONException{
		
	//	int count =0;
			
			File folder = new File(filePath);
			if (folder.exists() && folder.isDirectory()) {
				File[] files = folder.listFiles();
				for (File file : files) {
					//if(count>10)break;
					if (file.isFile()) {
						parseSingleFile(file);
						//count++;
					}
				}
			} else {
				System.out.println("Folder Path cannot be found");
			}
			
		}
		
	public void parseSingleFile(File file) throws FileNotFoundException{
		
		
		Scanner wordScanner = null;
		boolean isUrl = true;
		String url = null;
		String pageTitle=null, meta=null;
		//System.out.println(file.getName());
		wordScanner = new Scanner(file);
		if(!wordScanner.hasNextLine())return;
		url = wordScanner.nextLine();
		wordScanner.close();
		Document htmlFile = null;
		try {

			htmlFile = Jsoup.parse(file, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		pageTitle = htmlFile.select("title").text();
		meta = htmlFile.select("meta[name=description]").attr("content");
		//System.out.println(pageTitle+"\t"+meta);
		if(pageTitle=="")pageTitle="*^*";
		if(meta=="")meta="*^*";
		pageTitle= pageTitle.replaceAll("\\\\n","");
		
		Elements mainDiv = htmlFile.select("div.track");
		
		if(mainDiv.size()>0){
			for(Element currentDiv : mainDiv){
				
				String token = currentDiv.select("span").text();
				if(token.contains("{") && token.contains("}") && token.contains("object_type")){
					//System.out.println(token);
					token = token.trim();
					token = token.replaceAll("&quot;", "");
					JSONObject obj = null;
					try {
						obj = new JSONObject(token);
					} catch (JSONException e1) {
						System.out.println(token);
						e1.printStackTrace();
					}
					
					try {
						if(obj.has("object_type") ){
							Object current = obj.get("object_type");
							if(!current.getClass().equals(Integer.class))
								return;
							if(obj.getInt("object_type")==10)
							putData(obj,url,pageTitle,meta);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
//			wordScanner = new Scanner(file);
//			
//			while (wordScanner.hasNext()) {
//				token = wordScanner.nextLine();
//				if(isUrl){
//					url = token;
//					isUrl = false;
//					Document finaldoc = null;
//					try {
//						finaldoc = Jsoup.connect(url).get();
//					} catch (IOException e) {
//						System.out.println("BadURL");
//						continue;
//					} 
//					pageTitle = finaldoc.select("title").text();
//					meta = finaldoc.select("meta[name=description]").attr("content");
//					System.out.println(pageTitle+"\t"+meta);
//					if(pageTitle=="")pageTitle="*^*";
//					if(meta=="")meta="*^*";
//				}
//				
//				if(token.contains("{") && token.contains("}") && token.contains("object_type")){
//					//System.out.println(token);
//					token = token.trim();
//					token = token.replaceAll("&quot;", "");
//					JSONObject obj = null;
//					try {
//						obj = new JSONObject(token);
//					} catch (JSONException e1) {
//						System.out.println(token);
//						e1.printStackTrace();
//					}
//					
//					try {
//						if(obj.has("object_type") ){
//							Object current = obj.get("object_type");
//							if(!current.getClass().equals(Integer.class))
//								return;
//							if(obj.getInt("object_type")==10)
//							putData(obj,url,pageTitle,meta);
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				
//			}
//			
//			
//			
//			wordScanner.close();
	}
	
	public void writeToFile() throws IOException{
		
		DataOutputStream out = null;
		out = new DataOutputStream(new FileOutputStream("C:\\Users\\Aniruddha Kakade\\Desktop\\Academics\\IR\\Project\\data_new.txt"));
		SongData current;
		for(Map.Entry<String, SongData> entry : index.entrySet()){
			//out.writeBytes(entry.getKey());
			//out.writeBytes("\t");
			current = entry.getValue();
			out.writeBytes(entry.getKey()+";"+current.getTitle()+";"+current.getArtist()+";"+current.getAlbum()+";"+current.getAlbumArt()+";"+current.getYear()+";"+current.getGenre()+";"+current.getUrl()+";"+current.getPageTitle()+";"+current.getMeta()+"\n");
			
		}
		out.close();
	}
	
	public static void main(String[] args) throws JSONException, IOException {
		
		SongParser s1 = new SongParser();
		System.out.println("Parsing Data");
		s1.parseData();
		System.out.println("Writing to file");
		s1.writeToFile();
		
//		for(Map.Entry<String, SongData> entry : index.entrySet()){
//			System.out.println(entry.getValue().getTitle()+"\t"+entry.getValue().getArtist()+"\t"+entry.getValue().getAlbum());
//		}

	}

}
