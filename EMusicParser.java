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
 * Class for parsing Raw HTML Data from EMusic.com
 * 
 * @author Akshay
 *
 */
public class EMusicParser {

	String filePath = "C:\\My files\\Information Retrieval\\Music Sites Raw Data\\EmusicRawData";

	/* ArrayList containing song ID's */
	ArrayList<String> songIDList = new ArrayList<>();
	/* ArrayList containing song titles */
	ArrayList<String> songTitlesList = new ArrayList<>();
	/* ArrayList containing song artists */
	ArrayList<String> songArtistsList = new ArrayList<>();
	/* ArrayList containing song album art */
	ArrayList<String> songImageURLsList = new ArrayList<>();
	/* ArrayList containing song album */
	ArrayList<String> songAlbumList = new ArrayList<>();
	/* ArrayList containing song URL */
	ArrayList<String> urlList = new ArrayList<>();
	/* ArrayList containing song genre */
	ArrayList<String> songGenresList = new ArrayList<>();
	/* ArrayList containing song released year */
	ArrayList<String> songYearList = new ArrayList<>();
	/* HashMap mapping song ID against song data */
	ArrayList<String> metaPageTitlesList = new ArrayList<>();
	/* ArrayList containing meta tag description */
	ArrayList<String> metaDescriptionList = new ArrayList<>();
	static HashMap<String, SongData> index = new HashMap<>();
	static int countID = 0;

	/**
	 * Method for reading Raw HTML files
	 */
	public void readHtmlData() {
		File folder = new File(filePath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			int fileCnt = files.length;
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
	 * Method for parsing single HTML file and extracting text Data
	 * 
	 * @param file
	 *            :HTML file containing raw data
	 */
	private void parseSingleFile(File file) {
		String songTitle = "", songArtist = "", songAlbum = "", year = "", metaAttribute = "", songID = "";
		String imageURL = "", itemProperty = "", songURL = "", genre = "", date = "", metaPageTitle = "", metaDescription = "";
		String[] dateSplitter;
		Elements nestedInnerDiv, heading, anchor, metaTags, coverDiv, albumArtImage, wrapperDiv, tracksDiv, trackTR, trackTD;
		boolean metaTitleFlag = false, metaDescriptionFlag = false;
		Document htmlFile = null;
		try {

			/* Parsing the file using JSoup to extract text data */
			htmlFile = Jsoup.parse(file, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		metaPageTitle = htmlFile.getElementsByTag("title").text();
		metaTags = htmlFile.getElementsByTag("meta");
		metaTitleFlag = true;
		for (Element meta : metaTags) {
			metaAttribute = meta.attr("name");
			if (metaAttribute.contains("description")) {
				metaDescription = meta.attr("content").toString();
				metaDescriptionFlag = true;
				break;
			}

			if (!metaTitleFlag)
				metaPageTitle = "*^*";
			if (!metaDescriptionFlag)
				metaDescription = "*^*";
		}
		Elements div = htmlFile.select("div.catalog-stage");

		if (div != null) {
			// System.out.println(div);
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
				} else {
					genre = "*^*";
					songGenresList.add(genre);
				}
				if (itemProperty.contains("datePublished")) {
					date = eachMeta.attr("content").toString();
					dateSplitter = date.split("-");
					year = dateSplitter[0];
					songYearList.add(dateSplitter[0]);

				} else {
					year = "*^*";
					songYearList.add(year);
				}

				/*
				 * if (itemProperty.contains("name")) { songTitle =
				 * eachMeta.attr("content").toString();
				 * songTitlesList.add(songTitle); }
				 */
			}
			nestedInnerDiv = div.select("div.headline");
			songAlbum = nestedInnerDiv.select("hgroup > h1").text();
			songAlbumList.add(songAlbum);

			heading = nestedInnerDiv.select("hgroup > h2");
			anchor = heading.select("a");
			songArtist = anchor.select("span").text();
			songArtistsList.add(songArtist);

			wrapperDiv = div.select("div.wrap");
			tracksDiv = wrapperDiv.select("div#tracks");
			trackTR = tracksDiv.select("tr");
			for (Element tr : trackTR) {
				songID = tr.attr("data-id").toString();
				songIDList.add(songID);
				trackTD = tr.select("td.track-name");
				songTitle = trackTD.select("div.track").text();
				songTitlesList.add(songTitle);
			}

			coverDiv = div.select("div.cover");
			albumArtImage = coverDiv.select("img");
			imageURL = albumArtImage.attr("src").toString();
			songImageURLsList.add(imageURL);
			// insertData(songTitlesList, urlList, songAlbum, songArtist,
			// imageURL, genre, year,metaPageTitle, metaDescription);
			insertData(songIDList, songTitlesList, songURL, songAlbum,
					songArtist, imageURL, genre, year, metaPageTitle,
					metaDescription);
		}

	}

	/**
	 * Method for putting the data in the index map and SongData wrapper
	 * 
	 * @param songTitlesList2
	 *            :ArrayList holding all song titles
	 * @param songTitlesList3
	 * @param urlList2
	 *            :ArrayList holding all song URL's
	 * @param songAlbum2
	 *            :ArrayList holding all song albums
	 * @param songArtist2
	 *            :ArrayList holding all song artists
	 * @param imageURL2
	 *            :ArrayList holding all song album arts
	 * @param genre2
	 *            :ArrayList holding all song genre
	 * @param year2
	 *            :ArrayList holding all song years
	 * @param metaDescription2
	 * @param metaPageTitle2
	 */
	private void insertData(ArrayList<String> songIDList2,
			ArrayList<String> songTitlesList2, String urlList2,
			String songAlbum2, String songArtist2, String imageURL2,
			String genre2, String year2, String metaPageTitle2,
			String metaDescription2) {
		// String uniqueIdentifier = "em_";
		for (int i = 0; countID < songTitlesList2.size(); i++) {
			index.put(songIDList2.get(countID),
					new SongData(songTitlesList2.get(countID), urlList2,
							songAlbum2, songArtist2, year2, genre2, imageURL2,
							metaPageTitle2, metaDescription2));
			countID++;
		}

	}

	/**
	 * Driver function
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		EMusicParser sParser = new EMusicParser();
		sParser.readHtmlData();
		sParser.writeToFile();
		System.out.println("Written to file");
	}

	/**
	 * Method to write data from map to the file in text format
	 * 
	 * @throws IOException
	 */
	private void writeToFile() throws IOException {

		DataOutputStream out = null;
		out = new DataOutputStream(
				new FileOutputStream(
						"C:\\My files\\Information Retrieval\\ParsedTextData\\eMusicTextDataNew.txt"));
		SongData current;
		for (Map.Entry<String, SongData> entry : index.entrySet()) {
			out.writeBytes(entry.getKey());
			out.writeBytes(";");
			current = entry.getValue();
			out.writeBytes(current.getTitle() + ";" + current.getArtist() + ";"
					+ current.getAlbum() + ";" + current.getAlbumArt() + ";"
					+ current.getYear() + ";" + current.getGenre() + ";"
					+ current.getUrl().get(0) + ";" + current.getmetaTitle()
					+ ";" + current.getmetaDescription() + "\n");

		}
		out.close();

	}

}
