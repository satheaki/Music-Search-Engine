import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Akshay
 *
 */
public class SworlyParser {

	String filePath = "C:\\My files\\Information Retrieval\\Temp";
	ArrayList<String>songTitles=new ArrayList<>();
	ArrayList<String>songArtists=new ArrayList<>();
	ArrayList<String>songImageURLs=new ArrayList<>();
	static HashMap<String,SongData> index = new HashMap<String, SongData>();


	/**
	 * 
	 */
	public void parseHtmlData() {
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

	/**
	 * 
	 * @param file
	 */
	private void parseSingleFile(File file) {
		String songTitle = "", songArtist = "", divID = "", imageSrc = "";
		String id = "", imageURL = "";
		// Elements imgTags;
		Document htmlFile = null;
		try {

			htmlFile = Jsoup.parse(file, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element div = htmlFile.getElementById("horSongItems");
		Elements spans = div.getElementsByTag("span");
		Elements jpgs = div.getElementsByTag("img");
		// Elements allNestedDivs = div.getElementsByTag("div");

		for (Element ele : spans) {
			id = ele.attr("id");

			// System.out.println(ele);
			if (id.contains("horSongTitle")) {
				songTitle = ele.text();
				System.out.println("\nTitle:" + songTitle);
				songTitles.add(songTitle);
			}
			if (id.contains("horArtistTitle")) {
				songArtist = ele.text();
				System.out.println("\nArtist:" + songArtist);
				songArtists.add(songArtist);

			}
		}

		for (Element image : jpgs) {
			imageSrc = image.attr("src");
			imageURL = imageSrc.toString();
			System.out.println("Image URL:" + imageURL);
			songImageURLs.add(imageURL);
		}

		System.out.println("\n");
		putData(songTitles,songArtists,songImageURLs);
	}

	/**
	 * 
	 * @param songTitles2
	 * @param songArtists2
	 * @param songImageURLs2
	 */
	private void putData(ArrayList<String> songTitlesList,
			ArrayList<String> songArtistsList, ArrayList<String> songImageURLsList) {

		for(int i=0;i<songTitlesList.size();i++){
			String uuid=UUID.randomUUID().toString();
			index.put(uuid,new SongData(songTitlesList.get(i), "","", songArtistsList.get(i), "", "", songImageURLsList.get(i)));
		}
		
	}

	/**
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SworlyParser sParser = new SworlyParser();
		sParser.parseHtmlData();
		sParser.writeToFile();

	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void writeToFile() throws IOException {

		DataOutputStream out = null;
		out = new DataOutputStream(new FileOutputStream("C:\\My files\\Information Retrieval\\sworlyTextData.txt"));
		SongData current;
		for(Map.Entry<String, SongData> entry : index.entrySet()){
			out.writeBytes(entry.getKey());
			out.writeBytes(";");
			current = entry.getValue();
			out.writeBytes(current.getTitle()+";"+current.getArtist()+";"+current.getAlbum()+";"+current.getAlbumArt()+";"+current.getYear()+";"+current.getGenre()+";"+current.getUrl().get(0)+"\n");
			
		}
		out.close();
		
	}

}
