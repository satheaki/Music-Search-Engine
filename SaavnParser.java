
import java.io.*;
import java.util.*;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Bhoomika
 *
 */
public class SaavnParser {

    String filePath = "D:\\SaavnRawData\\";

    static DataOutputStream out = null;

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
                    System.out.println(file.getName());
                    parseSingleFile(file);
                }
            }
        } else {
            System.out.println("Folder Path cannot be found");
        }

    }

    private void parseSingleFile(File file) throws Exception {

        Document htmlFile = null;
        try {

            htmlFile = Jsoup.parse(file, "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Elements parents =htmlFile.getElementsByClass("cover");

        String imageurl = "*^*";

        String year = "*^*";
        String title = "*^*";
        String artist = "*^*";
        String url = "*^*";
        String pageTitle = "*^*";
        String description = "*^*";
        String songid = "*^*";
        String album = "*^*";
        boolean albumFlag = false;
        Elements tags = htmlFile.getElementsByAttribute("property");
        Elements titles = htmlFile.getElementsByTag("title");
        Elements metas = htmlFile.getElementsByAttribute("rel");
        Elements desc = htmlFile.getElementsByAttribute("name");
        for (Element d : desc) {
            String value = d.attr("name");
            if (value.equals("description")) {
                pageTitle = d.attr("content");
            }
        }
        for (Element meta : metas) {
            String name = meta.attr("rel");
            if (name.equals("canonical")) {
                String href = meta.attr("href");
                String arr[] = href.split("/");
                if (arr.length > 1) {
                    songid = arr[arr.length - 1];
                } else {
                    return;
                }

            }
        }
        for (Element Pagetitle : titles) {
            pageTitle = Pagetitle.html();
            System.out.println(pageTitle);
            break;
        }

        Elements parents = htmlFile.getElementsByTag("meta");

        if (parents.isEmpty()) {
            return;
        } else {
            //System.out.println();

            for (Element el : parents) {
                String prop = el.attr("property");
                String name = el.attr("name");
                if (name.equals("description")) {
                    description = el.attr("content");
                    System.out.println(el.attr("content"));
                    continue;
                }

                if (prop.equals("og:type")) {
                    System.out.println(el.attr("content"));
                    if (el.attr("content").equals("music.song") || el.attr("content").equals("music.album")) {
                        if(el.attr("content").equals("music.album")){
                            albumFlag = true;
                        }
                        continue;
                    } else {
                        return;
                    }
                }
                if (prop.equals("og:image")) {
                    imageurl = el.attr("content");
                    System.out.println(el.attr("content"));
                    continue;
                }
                if (prop.equals("og:title")) {
                    title = el.attr("content");
                    System.out.println(el.attr("content"));
                    continue;
                }
                if (prop.equals("og:url")) {
                    url = el.attr("content");
                    System.out.println(el.attr("content"));
                    continue;
                }
                if (prop.equals("music:album")) {

                    String albums[] = el.attr("content").split("/");
                    String albumName = albums[albums.length - 2];
                    String arr[] = albumName.split("-");
                    String str = "";
                    for (int i = 0; i < arr.length - 1; i++) {
                        str += arr[i];
                    }
                    album = str;
                    System.out.println(str);
                    continue;
                }
                if (prop.equals("music:release_date")) {
                    String date[] = el.attr("content").split("-");

                    year = date[0];
                    System.out.println(date[0]);
                    continue;
                }

               
            }
            boolean gotcha = false;

            Elements divs = htmlFile.getElementsByClass("header-content");
            for (Element el1 : divs) {
                if (gotcha) {
                    break;
                }
                Elements children = el1.children();
                for (Element e2 : children) {
                    if (gotcha) {
                        break;
                    }
                    if (e2.tagName().equals("a")) {
                        Elements ch = e2.children();
                        for (Element e3 : ch) {

                            artist = e3.html();
                            System.out.println(e3.html());
                            gotcha = true;
                            break;
                        }

                    }
                }
            }
            if(albumFlag){
                Elements divs1 = htmlFile.getElementsByClass("header-content");
                   for (Element el1 : divs) {

                Elements children = el1.children();
                for (Element e2 : children) {

                    if (e2.tagName().equals("h2")) {
                        artist = e2.html();
                        System.out.println(e2.html());

                    }
                }
            }
                   album = title;
            }
            
               SongData s = new SongData(title, url, album, artist,year, "*^*", imageurl);
                s.setPagetitle(pageTitle);
                 s.setDescrption(description);
                index.put(songid, s);
        }

    }

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
        out = new DataOutputStream(new FileOutputStream("D:\\saavn.txt"));
        SaavnParser sParser = new SaavnParser();
        sParser.parseHtmlData();
        sParser.writeToFile();

    }

}
