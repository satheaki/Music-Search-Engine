import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class CrawlerDemo extends WebCrawler{
	
	public static int count = 0;
	boolean append=true;
	public static String fileName = "C:\\My files\\Information Retrieval\\NewRawDataSworly\\sworly";
//	public static String fileName = "sworly";
	 private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
             + "|png|mp3|mp3|zip|gz))$");

	 @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !FILTERS.matcher(href).matches()
                && href.startsWith("http://sworly.com/");
     }

	 @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);
         url=url+"\n";
         
         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
           //  System.out.println(text);
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();
             String uuid=UUID.randomUUID().toString();
             //Store in raw file here
//             File file1 = new File(fileName+"_"+Integer.toString(count)+"_"+url+".txt");
             File file1 = new File(fileName+"_"+uuid+".html");
             count++;
             //File file2 = new File("gaana_text.txt");
             try {
				FileUtils.writeStringToFile(file1, html,append);
				//FileUtils.writeStringToFile(file2, text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
             System.out.println("Text length: " + text.length());
             //System.out.println("Html length: " + html.length());
//             System.out.println("Number of outgoing links: " + links.size());
         }
    }
}
