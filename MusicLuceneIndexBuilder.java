import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Class for creating index using Apache Lucene
 * 
 * @author Akshay
 */
public class MusicLuceneIndexBuilder {

	public IndexWriter indexWriter = null;

	/* Creates a new instance of Indexer */
	public MusicLuceneIndexBuilder() {
	}

	/**
	 * Method for creating a new Indexwriter Object if index is not present
	 * 
	 * @return :The indexWriter object
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
	 * Method for closing the indexwriter Object if not closed
	 * 
	 * @throws IOException
	 */
	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	/**
	 * Method for building the Lucene index
	 * 
	 * @param line
	 *            :Data from file containing song information
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public void indexSongData(String line, String fileName) throws IOException {
		String fullSearchableText = "", domain = "";
		double pageRank = -1;
		String[] domainSplitter;
		int pgRank = 0;
		//System.out.println(line);
		String[] splitter = line.split(";");
		IndexWriter writer = getIndexWriter();
		Document doc = new Document();
		
		pageRank = fetchPageRank(fileName);
		if(!splitter[0].contains("*^*")){
			doc.add(new TextField("ID", splitter[0], Field.Store.YES));
		}
		if (!splitter[1].contains("*^*")) {
			doc.add(new TextField("title", splitter[1], Field.Store.YES));
			fullSearchableText += splitter[1];
		}
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
			doc.add(new StringField("URL", splitter[7], Field.Store.YES));
			fullSearchableText += splitter[7];

			/*
			 * domainSplitter=splitter[7].split("/");
			 * domain=domainSplitter[0]+"//"+domainSplitter[2]; PageRankService
			 * prObj=new PageRankService();
			 * pgRank=prObj.getPR("http://gaana.com/");
			 * pageRank=Integer.toString(pgRank);
			 * 
			 * doc.add(new TextField("PageRank",pageRank,Field.Store.YES));
			 */
		}
		if (!splitter[8].contains("*^*")) {
			doc.add(new StringField("pageTitle", splitter[8], Field.Store.YES));
		}
		if (!splitter[9].contains("*^*")) {
			doc.add(new StringField("pageDescription", splitter[9],
					Field.Store.YES));
		}

		/*
		 * PageRankService prObj=new PageRankService();
		 * pgRank=prObj.getPR("http://gaana.com/");
		 * pageRank=Integer.toString(pgRank);
		 * System.out.println("Page Rank:"+pageRank);
		 */
		doc.add(new DoubleField("PageRank", pageRank,
				DOUBLE_FIELD_TYPE_STORED_SORTED));

		doc.add(new TextField("content", fullSearchableText, Field.Store.NO));
		writer.addDocument(doc);

	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private double fetchPageRank(String fileName) {
		double pR = -1;
		if (fileName.contains("gaana")) {
			pR = 7;
		} else if (fileName.contains("sworly")) {
			pR = 3;
		} else if (fileName.contains("EMusic")) {
			pR = 7;
		} else if (fileName.contains("jamendo")) {
			pR = 7;
		} else if (fileName.contains("shazam")) {
			pR = 6;
		} else if (fileName.contains("saavn")) {
			pR = 4;
		} else if (fileName.contains("indishuffle")) {
			pR = 5;
		} else if (fileName.contains("myspace")) {
			pR = 8;
		}
		return pR;
	}

	/**
	 * 
	 */
	private static final FieldType DOUBLE_FIELD_TYPE_STORED_SORTED = new FieldType();
	static {
		DOUBLE_FIELD_TYPE_STORED_SORTED.setTokenized(false);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
		DOUBLE_FIELD_TYPE_STORED_SORTED
				.setNumericType(FieldType.NumericType.DOUBLE);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setStored(true);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
		DOUBLE_FIELD_TYPE_STORED_SORTED.freeze();
	}

	/**
	 * Method for creating a new index object and indexing data from each line
	 * of file
	 * 
	 * @param fileDataList
	 *            :ArrayList containing each line read from file
	 * @param fileName
	 * @throws IOException
	 */
	public void createIndex(ArrayList<String> fileDataList, String fileName)
			throws IOException {

		getIndexWriter();

		for (String line : fileDataList) {
			indexSongData(line, fileName);
		}

		closeIndexWriter();
	}
}
