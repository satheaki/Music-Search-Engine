
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Akshay
 */
public class Searcher {
	private IndexSearcher searcher = null;
	private QueryParser parser = null;

	/* Creates a new instance of SearchEngine */
	public Searcher() throws IOException {
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory
				.open(Paths.get("indexDirectory"))));
		parser = new QueryParser("content", new StandardAnalyzer());
	}

	/**
	 * 
	 * @param docId
	 * @return
	 * @throws IOException
	 */
	public Document getDocument(int docId) throws IOException {
		return searcher.doc(docId);
	}

	/**
	 * 
	 * @param queryString
	 * @param n
	 * @param se
	 * @throws ParseException
	 * @throws IOException
	 */
	public void searchIndex(String queryString, int n, Searcher se)
			throws ParseException, IOException {
		 System.out.println("Searching for:" + queryString);
		Query query = parser.parse(queryString);
		TopDocs topDocs = searcher.search(query, n);

		System.out.println("Results found: " + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = se.getDocument(hits[i].doc);
			System.out.println("\nTitle:"+doc.get("title") + "\nArtist:" + doc.get("artist") + "\nAlbum: "
					+ doc.get("album") + "\nAlbumArt: " + doc.get("albumArt")+
					"\nYear:"+ doc.get("year") + "\nGenre:" + doc.get("genre") +"\nURL:"+ doc.get("URL")
					 +"Score Hits:" + "(" +hits[i].score + ")");

		}

		System.out.println("Search done");

	}
}
