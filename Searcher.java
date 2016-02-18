import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Class for searching the entry in Lucene index
 * 
 * @author Akshay
 */
public class Searcher {
	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private IndexReader reader=null;
	

	/* Creates a new instance of SearchEngine */
	public Searcher() {
		try{
			Directory dir=FSDirectory.open(Paths.get("C:\\My files\\Eclipse Workspace\\MusicSearchEngineIndexing\\indexDirectory"));
			System.out.println(dir);
			DirectoryReader dirReader=DirectoryReader.open(dir);
//		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory
//				.open(Paths.get("indexDirectory"))));
			searcher=new IndexSearcher(dirReader);
		parser = new QueryParser("content", new StandardAnalyzer());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Method for retrieving the document stored in Lucene index
	 * 
	 * @param docId
	 *            :Unique ID of each indexed document
	 * @return :Returns the results based on given Doc ID
	 * @throws IOException
	 */
	public Document getDocument(int docId) throws IOException {
		return searcher.doc(docId);
	}

	/**
	 * Method for searching the index for the given input query
	 * 
	 * @param queryString
	 *            :Query term to be searched in the index
	 * @param sort
	 *            :Total results to be retrieved
	 * @param se
	 *            :Searcher class object
	 * @throws ParseException
	 * @throws IOException
	 */
	public void searchIndex(String queryString,int n, Sort sort, Searcher se)
			throws ParseException, IOException {
		System.out.println("Searching for:" + queryString);
		Query query = parser.parse(queryString);
//		BM25Similarity bm25sim=new BM25Similarity(10.2f, 1.75f);
//		searcher.setSimilarity(bm25sim);
		TopFieldDocs topDocs = searcher.search(query, n, sort);
		
		
		System.out.println("Results found: " + topDocs.totalHits);
		System.out.println("Sorted By:"+sort);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = se.getDocument(hits[i].doc);
			System.out.println("\nTitle:" + doc.get("title") + "\nArtist:"
					+ doc.get("artist") + "\nAlbum: " + doc.get("album")
					+ "\nAlbumArt: " + doc.get("albumArt") + "\nYear:"
					+ doc.get("year") + "\nGenre:" + doc.get("genre")
					+ "\nURL:" + doc.get("URL") + "\nScore Hits:" + "("
					+ hits[i].score + ")"+"\nPageRank:"+doc.get("PageRank")+"\nID:"+doc.get("ID"));
		}

		System.out.println("Search done");
		
	}

	public void searchIndexLuceneOrder(String queryString, int n, Searcher se)
			throws ParseException, IOException {
		System.out.println("Searching for:" + queryString);
		Query query;
		
			query = parser.parse(queryString);
		
		TopDocs topDocs = searcher.search(query, n);

		System.out.println("Results found: " + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (int i = 0; i < hits.length; i++) {
			Document doc = se.getDocument(hits[i].doc);
			System.out.println("\nTitle:" + doc.get("title") + "\nArtist:"
					+ doc.get("artist") + "\nAlbum: " + doc.get("album")
					+ "\nAlbumArt: " + doc.get("albumArt") + "\nYear:"
					+ doc.get("year") + "\nGenre:" + doc.get("genre")
					+ "\nURL:" + doc.get("URL") + "\nScore Hits:" + "("
					+ hits[i].score + ")"+"\nmeta:"+doc.get("pageDescription"));

		}

		System.out.println("Search done");
		

	}
}
