import java.io.File;

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

	String filePath = "C:\\My files\\Information Retrieval\\RawDataSworly";

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
		String songTitle="",songArtist="",divID="",imageSrc="";
		String id="";
		Elements imgTags;
		Document htmlFile = null;
		try {
			
			htmlFile = Jsoup.parse(file, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element div=htmlFile.getElementById("horSongItems");
		Elements spans=div.getElementsByTag("span");
		Elements allNestedDivs=div.getElementsByTag("div");
	
		for(Element ele:spans){
			 	id=ele.attr("id");
//			 	System.out.println(ele);
			 	if(id.contains("horSongTitle")){
			 		songTitle=ele.text();
			 		System.out.println("\nTitle:"+songTitle);
			 	}
			 	else if(id.contains("horArtistTitle")){
			 		songArtist=ele.text();
					System.out.println("\nArtist:"+songArtist);

			 	}
			for(Element imgDiv:allNestedDivs){
				divID=imgDiv.attr("id");
				if(divID.contains("horSongImage")){
					imgTags=imgDiv.getElementsByTag("img");
					imageSrc=imgTags.attr("src");
				//	System.out.println(imageSrc);
				}
				
				
			}
		}
		
//		String artist=artistSpan.toString();
//		String albumArt=albumArtTag.attr("src");
		//System.out.println("\nTitle:"+title+" "+"Artist:"+artist+" "+"AlbumArt:"+albumArt);
		
//		System.out.println("\nArtist:"+artist);
//		System.out.println("\nImage:"+albumArt);
		System.out.println("\n");
		}
	

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SworlyParser sParser = new SworlyParser();
		sParser.parseHtmlData();

	}

}
