import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Akshay
 *
 */
public class EMusicParser {

	String filePath = "C:\\My files\\Information Retrieval\\Music Sites Raw Data\\Temp";
	ArrayList<String> songTitlesList = new ArrayList<>();
	ArrayList<String> songArtistsList = new ArrayList<>();
	ArrayList<String> songImageURLsList = new ArrayList<>();
	ArrayList<String> songAlbumList = new ArrayList<>();
	ArrayList<String> urlList = new ArrayList<>();
	ArrayList<String> songGenresList = new ArrayList<>();
	ArrayList<String> songYearList = new ArrayList<>();
	static HashMap<String, SongData> index = new HashMap<>();
	static int countID = 0;
	static int indicator = 0;

	/**
	 * 
	 */
	public void parseHtmlData() {
		File folder = new File(filePath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			int fileCnt = files.length;
			for (File file : files) {
				if (file.isFile()) {
					parseSingleFile(file, fileCnt);
				}
			}
		} else {
			System.out.println("Folder Path cannot be found");
		}

	}

	/**
	 * 
	 * @param file
	 * @param fileCnt
	 */
	private void parseSingleFile(File file, int fileCnt) {
		String songTitle = "", songArtist = "", divID = "", imageSrc = "", songAlbum = "", year = "";
		String id = "", imageURL = "", itemProperty = "", songURL = "", genre = "", date = "";
		String[] dateSplitter;
		Elements nestedInnerDiv, innerHeaderGroup, heading, anchor, metaTags, coverDiv, albumArtImage;
		Document htmlFile = null;
		try {

			htmlFile = Jsoup.parse(file, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Elements div = htmlFile.select("div.catalog-stage");

		if (div != null) {

			metaTags = div.select("meta");
			for (Element eachMeta : metaTags) {
				itemProperty = eachMeta.attr("itemprop");
				if (itemProperty.contains("url")) {
					songURL = eachMeta.attr("content").toString();
					urlList.add(songURL);
				}
				if (itemProperty.contains("genre")) {
					genre = eachMeta.attr("content").toString();
					songGenresList.add(genre);
				}
				if (itemProperty.contains("datePublished")) {
					date = eachMeta.attr("content").toString();
					dateSplitter = date.split("-");
					year = dateSplitter[0];
					songYearList.add(dateSplitter[0]);

				}
				if (itemProperty.contains("name")) {
					songTitle = eachMeta.attr("content").toString();
					songTitlesList.add(songTitle);
				}
			}
			nestedInnerDiv = div.select("div.headline");
			songAlbum = nestedInnerDiv.select("hgroup > h1").text();
			// System.out.println(songAlbum);
			songAlbumList.add(songAlbum);

			heading = nestedInnerDiv.select("hgroup > h2");
			anchor = heading.select("a");
			songArtist = anchor.select("span").text();
			songArtistsList.add(songArtist);

			coverDiv = div.select("div.cover");
			albumArtImage = coverDiv.select("img");
			imageURL = albumArtImage.attr("src").toString();
			songImageURLsList.add(imageURL);
//			 putData(songTitlesList, urlList, songAlbumList, songArtistsList,
//			 songImageURLsList, songGenresList, songYearList, fileCnt);
			insertData(songTitlesList, urlList, songAlbum, songArtist,
					imageURL, genre, year);
		}

	}

	private void insertData(ArrayList<String> songTitlesList2,
			ArrayList<String> urlList2, String songAlbum2, String songArtist2,
			String imageURL2, String genre2, String year2) {
		String uniqueIdentifier = "em_";
		for (int i = 0; countID<songTitlesList2.size(); i++) {
			index.put(uniqueIdentifier + Integer.toString(countID), new SongData(
					songTitlesList2.get(countID), urlList2.get(0), songAlbum2,
					songArtist2, year2, genre2, imageURL2));
			countID++;
		}

	}

	/*private void putData(ArrayList<String> songTitlesList2,
			ArrayList<String> urlList2, ArrayList<String> songAlbumList2,
			ArrayList<String> songArtistsList2,
			ArrayList<String> songImageURLsList2,
			ArrayList<String> songGenresList2, ArrayList<String> songYearList2,
			int fileCnt) {
		String uniqueIdentifier = "em_";
		for (int i = 0; i < songTitlesList2.size(); i++) {
			if (i >= urlList2.size() || i >= songAlbumList2.size()
					|| i >= songArtistsList2.size()
					|| i >= songImageURLsList2.size()
					|| i >= songGenresList2.size() || i >= songYearList2.size()) {
				indicator = i - 1;
				index.put(
						uniqueIdentifier + Integer.toString(i),
						new SongData(
								songTitlesList2.get(i),
								urlList2.get(urlList2.size() - 1),
								songAlbumList2.get(songAlbumList2.size() - 1),
								songArtistsList2.get(songArtistsList2.size() - 1),
								songYearList2.get(songYearList2.size() - 1),
								songGenresList2.get(songGenresList2.size() - 1),
								songImageURLsList2.get(songImageURLsList2
										.size() - 1)));

			} else {
				index.put(uniqueIdentifier + Integer.toString(i),
						new SongData(songTitlesList2.get(i), urlList2.get(i),
								songAlbumList2.get(i), songArtistsList2.get(i),
								songYearList2.get(i), songGenresList2.get(i),
								songImageURLsList.get(i)));
			}
		}

	}*/

	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		EMusicParser sParser = new EMusicParser();
		sParser.parseHtmlData();
		sParser.writeToFile();
		System.out.println("Written to file");
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void writeToFile() throws IOException {

		DataOutputStream out = null;
		out = new DataOutputStream(
				new FileOutputStream(
						"C:\\My files\\Information Retrieval\\ParsedTextData\\eMusicTextData.txt"));
		SongData current;
		for (Map.Entry<String, SongData> entry : index.entrySet()) {
			out.writeBytes(entry.getKey());
			out.writeBytes(";");
			current = entry.getValue();
			out.writeBytes(current.getTitle() + ";" + current.getArtist() + ";"
					+ current.getAlbum() + ";" + current.getAlbumArt() + ";"
					+ current.getYear() + ";" + current.getGenre() + ";"
					+ current.getUrl().get(0) + "\n");

		}
		out.close();

	}

}
