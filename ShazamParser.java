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
public class ShazamParser {

    String filePath = "D:\\ShazamRawData\\";

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

        
        Elements parents = htmlFile.getElementsByClass("tr-track");
      
        if (parents.isEmpty() || parents.size()>1) {
            return;
        } else {
            //System.out.println();
            String imageurl = "*^*";

            String title = "*^*";
            String artist = "*^*";
            String url = "*^*";
            String pageTitle = "*^*";
            String description = "*^*";
              Elements tags = htmlFile.getElementsByAttribute("property");
              Elements titles = htmlFile.getElementsByTag("title");
              Elements metas = htmlFile.getElementsByTag("meta");
              for(Element meta:metas){
                  String name = meta.attr("name");
                  if(name.equals("description")){
                      System.out.println();
                      description = meta.attr("content");
                  }
              }
              for(Element Pagetitle:titles){
                  pageTitle = Pagetitle.html();
                  System.out.println(pageTitle);
                  break;
              }
        for(Element tg1:tags){
            if(tg1.attr("property").equals("al:web:url")){
                url = tg1.attr("content");
            }
        }
            for (Element e : parents) {
                Elements e1 = e.getElementsByClass("tr-cover-art");
                for (Element e2 : e1) {
                    Elements e3 = e2.children();
                    for (Element e4 : e3) {
                        imageurl = e4.attr("src");
                       
                        
                       // System.out.println();
                    }
                }
                Elements f1 = e.getElementsByClass("tr-details__container");
                boolean flag = false;
                for (Element f2 : f1) {
                    if (flag) {
                        break;
                    }
                    Elements f3 = f2.children();
                    for (Element f4 : f3) {
                        if (flag) {
                            break;
                        }
                        Elements f5 = f4.children();
                        for (Element f6 : f5) {
                            if (flag) {
                                break;
                            }
                            Elements f7 = f6.children();
                            //Elements f8 = f7;//.children();
                            for (Element f8 : f7) {
                                if (flag) {
                                    break;
                                }
                                Elements f9 = f8.children();
                                for (Element f10 : f9) {

                                    String str = f10.attr("href");
                                    flag = true;
                                    url = str;
                                    break;
                                    // System.out.println(str);
                                }
                            }
                        }
                    }
                }

            }
           // System.out.println();

            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            String str = "";
            boolean flag = false;
            while ((line = br.readLine()) != null) {
                if (line.contains("</script>")) {
                    flag = false;
                }
                if (flag) {
                    str = str + line;
                }
                if (line.contains("Shazam.config['advert'] = {")) {
                    //str = str + line;
                    flag = true;
                }

            }
            str = str.replaceAll("\\s+", "");
            JSONObject obj = null;
            try{
                obj = new JSONObject("{" + str);
            }
            catch(Exception e){
               // e.printStackTrace();
                //System.out.println();
                return;
            }
            
            JSONObject obj1 = obj.getJSONObject("targets");
            String tid = "*^*";
            String gr = "*^*";
            String album = "*^*";
            artist = "*^*";
            
            try{
                tid = obj1.getString("tid");}
            catch(Exception e){
                
               // System.out.println();
            }
            try{
                gr = obj1.getString("gr");
            
            }
            catch(Exception e){
                //System.out.println();
            }
            try{
                artist = obj1.getString("an");

            }
            catch(Exception e){
               // System.out.println();
            }
            try{
                            title = obj1.getString("tt");
            
            }
            catch(Exception e){
                //System.out.println();
            }
            try{
                album = obj1.getString("rl");
            }
            catch(Exception e){
               // System.out.println();
            }
            
             if(title.equals("")){
                        title = "*^*";
                    }
                          if(artist.equals("")){
                        artist = "*^*";
                    }
                                       if(gr.equals("")){
                        gr = "*^*";
                    }
                    if(url.equals("")){
                        url = "*^*";
                    }
                    if(tid.equals("")){
                        return;
                    }
                    if(album.equals("")){
                        album = "*^*";
                    }                    
                    if(imageurl.equals("")){
                        imageurl = "*^*";
                    }
            SongData s = new SongData(title, url,album, artist, "*^*", gr, imageurl);
            s.setPagetitle(pageTitle);
            s.setDescrption(description);
            index.put(tid, s);
            
        }

        
    }

    public void writeToFile() throws IOException {

        SongData current;
        for (Map.Entry<String, SongData> entry : index.entrySet()) {
            out.writeBytes(entry.getKey());
            out.writeBytes(";");
            current = entry.getValue();
            out.writeBytes(current.getTitle() + ";" + current.getArtist() + ";" + current.getAlbum() + ";" + current.getAlbumArt() + ";" + current.getYear() + ";" + current.getGenre() + ";" + current.getUrl().get(0) +";"+current.getPagetitle()+";"+current.getDescrption()+ "\n");

        }
    }

    public static void main(String[] args) throws Exception {
        out = new DataOutputStream(new FileOutputStream("D:\\shazam.txt"));
        ShazamParser sParser = new ShazamParser();
        sParser.parseHtmlData();
        sParser.writeToFile();

    }

}
