import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Driver class for processing text files and index
 * 
 * @author Akshay
 */
public class MusicLuceneIndexProcessor {

	String filePath = "C:\\My files\\Information Retrieval\\ParsedTextData\\";
//	String filePath = "C:\\My files\\Information Retrieval\\Testparse\\";

	public static void main(String[] args) throws IOException, ParseException {
		MusicLuceneIndexProcessor processor = new MusicLuceneIndexProcessor();
		processor.readFiles();
	}

	/**
	 * Method for reading text files
	 */
	public void readFiles() {
		File folder = new File(filePath);
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					System.out.println(file.getName());					
					parseSingleFile(file);
				}
			}
		} else {
			System.out.println("\nFolder Path cannot be found");
		}
		System.out.println("\nIndex built successfully");
	}

	
	/**
	 * Method for parsing single text file and processing file line by line
	 * 
	 * @param file
	 *            :Text file of songs
	 */
	private void parseSingleFile(File file) {
		BufferedReader br = null;
		ArrayList<String> fileData = new ArrayList<>();
		try {
			System.out.println("\nBuilding index");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				// line = line.toLowerCase();
				fileData.add(line);

			}
			MusicLuceneIndexBuilder indexer = new MusicLuceneIndexBuilder();
			indexer.createIndex(fileData, file.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
