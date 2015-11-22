import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
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
	public static String fileName = "C:\\Users\\Aniruddha Kakade\\Desktop\\Academics\\IR\\Project\\raw\\gaana";
	 private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
             + "|png|mp3|mp3|zip|gz))$");

	 @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         return !FILTERS.matcher(href).matches()
                && href.startsWith("http://gaana.com/");
     }

	 @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("URL: " + url);
         url = url +"\n";
         
         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();
             String uuid = UUID.randomUUID().toString();
             //Store in raw file here
             File file1 = new File(fileName+"_"+uuid+".txt");
//             try {
//				Files.setAttribute(file1.toPath(), "url", url);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}       
             count++;         
             try {
            	 String data = url + text;
            	FileUtils.writeStringToFile(file1, data);
				//FileUtils.writeStringToFile(file1, text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
             //System.out.println("Text length: " + text.length());
             //System.out.println("Html length: " + html.length());
//             System.out.println("Number of outgoing links: " + links.size());
         }
    }
}
