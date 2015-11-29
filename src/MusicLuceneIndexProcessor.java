import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Akshay
 */
public class MusicLuceneIndexProcessor {

	String filePath = "C:\\My files\\Information Retrieval\\ParsedTextData\\";

	public static void main(String[] args) throws IOException, ParseException {
		MusicLuceneIndexProcessor processor = new MusicLuceneIndexProcessor();
		processor.readFiles();
		System.out.println("performSearch");
		Searcher se = new Searcher();
		se.searchIndex("Sputnik", 20, se);
		//se.searchIndex("Paradise",10, se);
//		se.searchIndex("Dil se",100, se);

		// se.searchIndex("Hotel Paris", 2, se);
		// se.searchIndex("Hotel Paris",10,se);

	}

	/**
	 * 
	 */
	public void readFiles() {
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
		BufferedReader br = null;
		ArrayList<String> fileData = new ArrayList<>();
		try {
			// build a lucene index
			System.out.println("Creating Indexes");
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				line = line.toLowerCase();
				fileData.add(line);

			}
			MusicLuceneIndexBuilder indexer = new MusicLuceneIndexBuilder();
			indexer.createIndex(fileData);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
