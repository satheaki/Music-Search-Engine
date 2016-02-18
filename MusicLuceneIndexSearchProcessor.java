import java.io.IOException;

import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

/**
 * Driver class for Searching the Lucene index
 * 
 * @author Akshay
 */
public class MusicLuceneIndexSearchProcessor {

	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("Searching...");
		

		Searcher se = new Searcher();
		/*se.searchIndex("wake me up when september ends", 10, new Sort(new SortField[] {
				SortField.FIELD_SCORE,
				new SortField("PageRank",SortField.Type.DOUBLE,true),
				 }), se);*/

		 se.searchIndexLuceneOrder("",10,se);

	}

}
