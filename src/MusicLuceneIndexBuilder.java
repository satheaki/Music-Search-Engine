import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Akshay
 */
public class MusicLuceneIndexBuilder {

	public IndexWriter indexWriter = null;

	/* Creates a new instance of Indexer */
	public MusicLuceneIndexBuilder() {
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public IndexWriter getIndexWriter() throws IOException {
		if (indexWriter == null) {

			File fs = new File("indexDirectory");
			Directory indexDir = FSDirectory.open(Paths.get("indexDirectory"));
			IndexWriterConfig config = new IndexWriterConfig(
					new StandardAnalyzer());
			indexWriter = new IndexWriter(indexDir, config);
		}
		return indexWriter;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	/**
	 * 
	 * @param line
	 * @throws IOException
	 */
	public void indexSongData(String line) throws IOException {
		String fullSearchableText = "";
		String[] splitter = line.split(";");
		IndexWriter writer = getIndexWriter();
		Document doc = new Document();

		doc.add(new TextField("title", splitter[1], Field.Store.YES));
		fullSearchableText += splitter[1];
		if (!splitter[2].contains("*^*")) {
			doc.add(new TextField("artist", splitter[2], Field.Store.YES));
			fullSearchableText += splitter[2];
		}
		if (!splitter[3].contains("*^*")) {
			doc.add(new TextField("album", splitter[3], Field.Store.YES));
			fullSearchableText += splitter[3];
		}
		if (!splitter[4].contains("*^*")) {
			doc.add(new TextField("albumArt", splitter[4], Field.Store.YES));
			fullSearchableText += splitter[4];
		}
		if (!splitter[5].contains("*^*")) {
			doc.add(new TextField("year", splitter[5], Field.Store.YES));
			fullSearchableText += splitter[5];
		}
		if (!splitter[6].contains("*^*")) {
			doc.add(new TextField("genre", splitter[6], Field.Store.YES));
			fullSearchableText += splitter[6];
		}
		if (!splitter[7].contains("*^*")) {
			doc.add(new TextField("URL", splitter[7], Field.Store.YES));
			fullSearchableText += splitter[7];
		}

		doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
		writer.addDocument(doc);

	}

	/**
	 * 
	 * @param fileDataList
	 * @throws IOException
	 */
	public void createIndex(ArrayList<String> fileDataList) throws IOException {

		getIndexWriter();

		for (String line : fileDataList) {
			indexSongData(line);
		}
		closeIndexWriter();
	}
}
