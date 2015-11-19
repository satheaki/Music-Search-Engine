import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;


public class SongParser {

	HashMap<String, String> songListMap = new HashMap<>();
	static HashMap<String,SongData> index = new HashMap<String, SongData>();
	
	String filePath = "C:\\Users\\Aniruddha Kakade\\Desktop\\Academics\\IR\\Project\\raw\\";
	//static int count = 0;
	
	public void putData(JSONObject obj, String url) throws JSONException{
		
		String songId = obj.getString("id");
		String title= obj.getString("title");
		String album = obj.getString("albumtitle");
		String artist = obj.getString("artist");
		String albumart = obj.getString("albumartwork");
		
		artist = artist.replaceAll("[^a-zA-Z-]+", " ");
				
				if(!index.containsKey(songId)){
					index.put(songId, new SongData(title, url, album, artist, "none", "none", albumart));
				}
	}
	
	
	public void parseData() throws FileNotFoundException, JSONException{
		
		
			
			File folder = new File(filePath);
			if (folder.exists() && folder.isDirectory()) {
				File[] files = folder.listFiles();
				for (File file : files) {
					if (file.isFile()) {
						parseSingleFile(file);
					}
				}
			} else {
				System.out.println("Folder Path cannot be found");
			}
			
		}
		
	public void parseSingleFile(File file) throws FileNotFoundException{
		
		String token;
		Scanner wordScanner = null;
		
		String url = "none";
		
			wordScanner = new Scanner(file);
			
			while (wordScanner.hasNext()) {
				token = wordScanner.nextLine();
				
				if(token.contains("{")){
					//System.out.println(token);
					token = token.trim();
					JSONObject obj = null;
					try {
						obj = new JSONObject(token);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// && obj.getInt("object_type")==10
					try {
						if(obj.has("object_type") ){
							Object current = obj.get("object_type");
							if(!current.getClass().equals(Integer.class))
								return;
							if(obj.getInt("object_type")==10)
							putData(obj,url);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
			
			
			
			wordScanner.close();
	}
	
	public void writeToFile() throws IOException{
		
		DataOutputStream out = null;
		out = new DataOutputStream(new FileOutputStream("C:\\Users\\Aniruddha Kakade\\Desktop\\Academics\\IR\\Project\\data.txt"));
		SongData current;
		for(Map.Entry<String, SongData> entry : index.entrySet()){
			out.writeBytes(entry.getKey());
			out.writeBytes("\t");
			current = entry.getValue();
			out.writeBytes(current.getTitle()+";"+current.getArtist()+";"+current.getAlbum()+";"+current.getAlbumArt()+";"+current.getYear()+";"+current.getGenre()+";"+current.getUrl().get(0)+"\n");
			
		}
		
//		Properties properties = new Properties();
//		for (Map.Entry<String,SongData> entry : index.entrySet()) {
//		    properties.put(entry.getKey(), entry.getValue());
//		}
//
//		try {
//			properties.store(new FileOutputStream("data.properties"), null);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) throws JSONException, IOException {
		
		SongParser s1 = new SongParser();
		s1.parseData();
		s1.writeToFile();
		
//		for(Map.Entry<String, SongData> entry : index.entrySet()){
//			System.out.println(entry.getValue().getTitle()+"\t"+entry.getValue().getArtist()+"\t"+entry.getValue().getAlbum());
//		}

	}

}
