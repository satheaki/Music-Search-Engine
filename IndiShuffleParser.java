
import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Bhoomika
 *
 */
public class IndiShuffleParser {

    String filePath = "D:\\UTDallas\\Information Retireval\\Project\\Music-Search-Engine-master\\indieshuffleRawdata\\";

    static DataOutputStream out = null;

    static int playListCounter = 1;
    static HashMap<String, SongData> index = new HashMap<String, SongData>();

    /**
     *
     */
    public void parseHtmlData() throws Exception {
        File folder = new File(filePath);
        //  parseSingleFile(folder);
        //parseSingleFile(folder);
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
    private void parseSingleFile(File file) throws Exception {

        System.out.println(file.getName());
        ArrayList<String> songName = new ArrayList<String>();
        ArrayList<String> songArtist = new ArrayList<String>();
        ArrayList<String> AlbumArtWork = new ArrayList<String>();
        ArrayList<String> AudioURL = new ArrayList<String>();

        //String songTitle=" ",songArtist="",divID="",imageSrc="";
        String id = "";
        Elements imgTags;
        Document htmlFile = null;
        try {

            htmlFile = Jsoup.parse(file, "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line = null;
        boolean playList = false;
        while ((line = br.readLine()) != null) {

            if (line.contains("\"@type\": \"MusicPlaylist\"")) {
                //System.out.println(line);
                playList = true;

                break;
            }
        }

        br.close();
        if (playList) {
            String playListId = "Indie_"+playListCounter;
            playListCounter++;
            String pageTitle = "*^*";
            String description = "*^*";
            String url = "*^*";
            String title = "*^*";
            Elements tags = htmlFile.getElementsByAttribute("property");
            Elements rels = htmlFile.getElementsByAttribute("rel");
            Elements titles = htmlFile.getElementsByTag("title");
            Elements metas = htmlFile.getElementsByTag("meta");
             for (Element rel : rels) {
                String name = rel.attr("rel");
                if (name.equals("canonical")) {
                    System.out.println();
                    url = rel.attr("href");
                }
            }
             String imageurl = "*^*";
             String year = "*^*";
             Elements years = htmlFile.getElementsByTag("span");
             for(Element span1:years){
                 if(span1.attr("class").equals("ash collection-info")){
                     Elements spans = span1.children();
                 for(Element span:spans){
                     if(span.attr("class").equals("playlist-publish-date")){
                         String arr[] = span.html().split(",");
                         year = arr[arr.length-1];
                     }
                 }
                 }
             }
             for(Element espan:years){
                 
             }
            for (Element meta : metas) {
                String name = meta.attr("name");
                String prop = meta.attr("property");
                if (prop.equals("og:image")) {
                   // System.out.println();
                    imageurl = meta.attr("content");
                }

                if (name.equals("description")) {
                   // System.out.println();
                    description = meta.attr("content");
                }
            }
            for (Element Pagetitle : titles) {
                pageTitle = Pagetitle.html();
                String arr[] = pageTitle.split("\"");
                title = arr[1];
                //System.out.println(pageTitle);
                break;
            }
              if(title.equals("")){
                        title = "*^*";
                    }
                    if(url.equals("")){
                        url = "*^*";
                    }
                    if(year.equals("")){
                        year = "*^*";
                    }
                    if(imageurl.equals("")){
                        imageurl = "*^*";
                    }
              
                    SongData s = new SongData(title, url, "*^*", "*^*", year, "*^*", imageurl);
                    s.setPagetitle(pageTitle);
                    s.setDescrption(description);
                    
                    index.put(playListId, s);
                    System.out.println(index.get("Indie_1"));

            return;
        }

        Elements parents = htmlFile.getElementsByClass("cover");
        if (parents.isEmpty()) {
            return;
        } else {
            //System.out.println();
        }
        String pageTitle = "*^*";
        String description = "*^*";
        Elements tags = htmlFile.getElementsByAttribute("property");
        Elements titles = htmlFile.getElementsByTag("title");
        Elements metas = htmlFile.getElementsByTag("meta");
        for (Element meta : metas) {
            String name = meta.attr("name");
            if (name.equals("description")) {
               // System.out.println();
                description = meta.attr("content");
            }
        }
        for (Element Pagetitle : titles) {
            pageTitle = Pagetitle.html();
           // System.out.println(pageTitle);
            break;
        }
        for (Element e : parents) {
            Elements ch = e.children();
            int count = 1;
            for (Element e1 : ch) {
                if (count == 2) {
                    String songURL = "http://www.indieshuffle.com" + e1.attr("data-url");
                    String title = e1.attr("data-track-title");
                    String artist = e1.attr("data-track-artist");
                    String artwork = e1.attr("data-artwork");
                    String songid = e1.attr("id");
                    if(title.equals("")){
                        title = "*^*";
                    }
                    if(artist.equals("")){
                        artist = "*^*";
                    }
                    if(artwork.equals("")){
                        artwork = "*^*";
                    }
                    if(songid.equals("")){
                        songid = "*^*";
                    }
                 
                    SongData s = new SongData(title, songURL, "*^*", artist, "*^*", "*^*", artwork);
                    s.setPagetitle(pageTitle);
                    s.setDescrption(description);
                    index.put(songid, s);
                    // System.out.println(songURL);
                } else {

                    count++;
                    continue;
                }
                count++;

            }
        }

        
    }

    /**
     *
     * @param args
     */
    public void writeToFile() throws IOException {

        SongData current;
        for (Map.Entry<String, SongData> entry : index.entrySet()) {
            out.writeBytes(entry.getKey());
            out.writeBytes(";");
            current = entry.getValue();
            out.writeBytes(current.getTitle() + ";" + current.getArtist() + ";" + current.getAlbum() + ";" + current.getAlbumArt() + ";" + current.getYear() + ";" + current.getGenre() + ";" + current.getUrl().get(0) + ";" + current.getPagetitle() + ";" + current.getDescrption() + "\n");

        }
    }

    public static void main(String[] args) throws Exception {
        out = new DataOutputStream(new FileOutputStream("D:\\indishuffle.txt"));
        IndiShuffleParser sParser = new IndiShuffleParser();
        sParser.parseHtmlData();
        sParser.writeToFile();
    }

}
