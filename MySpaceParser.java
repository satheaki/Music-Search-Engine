/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bhoomika
 */
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Akshay
 *
 */
public class MySpaceParser {

    String filePath = "D:\\myspacefiles\\";

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

        Elements parents = htmlFile.getElementsByTag("section");

        String title = "*^*";
        String artist = "*^*";
        String url = "*^*";
        String imageurl = "*^*";
        String pageTitle = "*^*";
        String description = "*^*";
        String songid = "*^*";
        String genre = "*^*";
        String album = "*^*";
        String year = "*^*";
           boolean isVideo = false;
            Elements titles = htmlFile.getElementsByTag("title");
            Elements metas = htmlFile.getElementsByTag("meta");
            for (Element meta : metas) {
                String name = meta.attr("name");
                String prop = meta.attr("property");
                if (prop.equals("og:video")) {
                     System.out.println();
                     url = meta.attr("content");
                     String arr[] = url.split("/");
                     songid = arr[arr.length-1];
                     title = arr[arr.length-2];
                     artist = arr[arr.length-4];
                    isVideo = true;
                }
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

            if(isVideo){
                 SongData s = new SongData(title, url, album, artist, year, genre, imageurl);
                                    s.setPagetitle(pageTitle);
                                    s.setDescrption(description);
                                    index.put(songid, s);
                                    return;
            }
        if (parents.isEmpty() && !isVideo) {
            return;
        } else {
           // boolean isVideo = false;
            titles = htmlFile.getElementsByTag("title");
            metas = htmlFile.getElementsByTag("meta");
            for (Element meta : metas) {
                String name = meta.attr("name");
                String prop = meta.attr("property");
                if (prop.equals("og:video")) {
                     System.out.println();
                     url = meta.attr("content");
                     String arr[] = url.split("/");
                     songid = arr[arr.length-1];
                    isVideo = true;
                }
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
                if (e.attr("id").equals("song")) {
                    Elements e1 = e.children();
                    for (Element e2 : e1) {
                        if (e2.attr("id").equals("actions")) {
                            Elements e3 = e2.children();
                            int count = 0;
                            for (Element e4 : e3) {

                                if (count == 1) {
                                    songid = e4.attr("data-song-id");
                                    album = e4.attr("data-album-title");
                                    title = e4.attr("data-title");
                                    artist = e4.attr("data-artist-name");
                                    url = "www.myspace.com" + e4.attr("data-url");
                                    genre = e4.attr("data-genre-name");
                                    imageurl = e4.attr("data-image-url");
                                    SongData s = new SongData(title, url, album, artist, year, genre, imageurl);
                                    s.setPagetitle(pageTitle);
                                    s.setDescrption(description);
                                    index.put(songid, s);
                                }
                                count++;
                            }
                            // System.out.println();
                        }
                    }

                    // System.out.println(e.attr("id"));
                }

            }
            //System.out.println();

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
        out = new DataOutputStream(new FileOutputStream("D:\\myspace.txt"));
        MySpaceParser sParser = new MySpaceParser();
        sParser.parseHtmlData();
        sParser.writeToFile();

    }

}
