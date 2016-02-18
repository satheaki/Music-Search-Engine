import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class for parsing Raw HTML Data from Jamendo.com
 * 
 * @author Akshay
 *
 */
public class JamendoParserNew {

	String filePath = "C:\\My files\\Information Retrieval\\Music Sites Raw Data\\JamendoData";
	// String filePath =
	// "C:\\My files\\Information Retrieval\\Music Sites Raw Data\\TempTest";

	/* ArrayList containing song ID's */
	ArrayList<String> songIDList = new ArrayList<>();
	/* ArrayList containing song titles */
	ArrayList<String> songTitlesList = new ArrayList<>();
	/* ArrayList containing song artists */
	ArrayList<String> songArtistsList = new ArrayList<>();
	/* ArrayList containing song album */
	ArrayList<String> songAlbumList = new ArrayList<>();
	/* ArrayList containing song album art */
	ArrayList<String> songAlbumArtList = new ArrayList<>();
	/* ArrayList containing song URL */
	ArrayList<String> songUrlList = new ArrayList<>();
	/* ArrayList containing song genre */
	ArrayList<String> songGenreList = new ArrayList<>();
	/* ArrayList containing song released year */
	ArrayList<String> songYearList = new ArrayList<>();
	/* ArrayList containing meta tag title */
	ArrayList<String> metaPageTitlesList = new ArrayList<>();
	/* ArrayList containing meta tag description */
	ArrayList<String> metaDescriptionList = new ArrayList<>();
	static HashMap<String, SongData> index = new HashMap<>();
	ArrayList<String> allGenreList = new ArrayList<>();
	static int countID = 0;

	/**
	 * Method for reading Raw HTML file
	 */
	public void readHtmlData() {
		File folder = new File(filePath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			Arrays.sort(files);
			for (File file : files) {
				if (file.isFile()) {
					System.out.println(file.getName());
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
		Elements trackInfoDiv, nestedTitleSpan, nestedULTrackTag, liTags, urlAnchorTag, genreLi;
		String songURL = "", songGenre = "", songID = "", songArtist, songTitle = "", songAlbum = "";
		String songYear = "", songAlbumArt = "", metaPageTitle = "", metaDescription = "", metaAttribute = "";
		String[] songIdSplitter;
		boolean metaTitleFlag = false, metaDescriptionFlag = false;
		Document htmlFile = null;

		try {

			htmlFile = Jsoup.parse(file, "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String htmlTagLang = htmlFile.getElementsByTag("html").attr("lang");
		// if (htmlTagLang.contains("en")) {
		metaPageTitle = htmlFile.getElementsByTag("title").text();
		Elements metaTags = htmlFile.getElementsByTag("meta");
		metaTitleFlag = true;
		for (Element meta : metaTags) {
			metaAttribute = meta.attr("name");
			if (metaAttribute.contains("description")) {
				metaDescription = meta.attr("content").toString();
				metaDescriptionFlag = true;
			}

			if (!metaTitleFlag)
				metaPageTitle = "*^*";
			if (!metaDescriptionFlag)
				metaDescription = "*^*";

		}

		Elements ul = htmlFile.select("ul.row");
		if (ul.size() != 0) {
			liTags = ul.select("li.track");
			for (Element li : liTags) {
				urlAnchorTag = li.select("a.link-wrap");
				songURL = urlAnchorTag.attr("href").toString();
				songURL = "https://www.jamendo.com" + songURL;
				songUrlList.add(songURL);

				songIdSplitter = songURL.split("/");
				songID = songIdSplitter[4];
				songIDList.add(songID);

				trackInfoDiv = urlAnchorTag.select("div.track_information");
				nestedTitleSpan = trackInfoDiv
						.select("span.track_information_title");
				songTitle = nestedTitleSpan.text();
				songTitlesList.add(songTitle);

				songArtist = trackInfoDiv.select(
						"span.track_information_artist").text();
				songArtistsList.add(songArtist);

				songAlbum = "*^*";
				songAlbumList.add(songAlbum);

				songAlbumArt = "*^*";
				songAlbumArtList.add(songAlbumArt);

				songYear = "*^*";
				songYearList.add(songYear);

				nestedULTrackTag = urlAnchorTag.select("ul.track_tags");
				genreLi = nestedULTrackTag.select("li");
				for (Element nestedli : genreLi) {
					songGenre = nestedli.text();
				}
				if (!songGenre.contains(""))
					songGenreList.add(songGenre);
				else {
					songGenre = "*^*";
					songGenreList.add(songGenre);
				}

			}
		}

		putData(songIDList, songTitlesList, songArtistsList, songAlbumList,
				songAlbumArtList, songYearList, songGenreList, songUrlList,
				metaPageTitle, metaDescription);

		// }
	}

	/**
	 * Method for putting the data in the index map and SongData wrapper
	 * 
	 * @param songIDList2
	 *            :ArrayList holding all song unique ID's
	 * @param songTitlesList2
	 *            :ArrayList holding all song titles
	 * @param songArtistsList2
	 *            :ArrayList holding all song artists
	 * @param songAlbumList2
	 *            :ArrayList holding all song URL's
	 * @param songAlbumArtList2
	 *            :ArrayList holding all song album arts
	 * @param songYearList2
	 *            :ArrayList holding all song years
	 * @param songGenreList2
	 *            :ArrayList holding all song genre
	 * @param songUrlList2
	 *            :ArrayList holding all song URL's
	 * @param metaDescription
	 * @param metaPageTitle
	 */
	private void putData(ArrayList<String> songIDList2,
			ArrayList<String> songTitlesList2,
			ArrayList<String> songArtistsList2,
			ArrayList<String> songAlbumList2,
			ArrayList<String> songAlbumArtList2,
			ArrayList<String> songYearList2, ArrayList<String> songGenreList2,
			ArrayList<String> songUrlList2, String metaPageTitle,
			String metaDescription) {
		for (int i = 0; i < songIDList2.size(); i++) {
			index.put(songIDList2.get(i), new SongData(songTitlesList2.get(i),
					songUrlList2.get(i), songAlbumList2.get(i),
					songArtistsList2.get(i), songYearList2.get(i),
					songGenreList2.get(i), songAlbumArtList2.get(i),
					metaPageTitle, metaDescription));
		}

	}

	/**
	 * Driver function
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		JamendoParserNew sParser = new JamendoParserNew();
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
						"C:\\My files\\Information Retrieval\\ParsedTextData\\jamendoTextData.txt"));
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
