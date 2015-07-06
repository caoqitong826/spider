package com.makenv.spiderInternet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caoqitong on 3/26/2015.
 */
public class Spider1 {
    private static Set<String> urlSet = new HashSet<String>();

    private static Pattern p = Pattern
            .compile(
                    "^(((http|https)://" +
                            "(www.|([1-9]|[1-9]\\d|1\\d{2}|2[0-1]\\d|25[0-5])" +
                            "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}:[0-9]+/)?)" +
                            "{1}.+){1}quot;",
                    Pattern.CASE_INSENSITIVE);
//        private static Pattern p = Pattern.compile("^[a-zA-z]+://[^\\s]*");

    public static void main(String[] args) {
        String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/index.html";
        spiderInternet(baseUrl, "");
    }

    private static void spiderInternet(String baseUrl, String exUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        if (baseUrl.endsWith(".html") && exUrl!="") {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/')+1);
        }
        String new_url = baseUrl + exUrl;
        if (urlSet.contains(new_url)) {
            return;
        }
        Elements elements = null;
        try {
            // Document doc = Jsoup.connect(new_url).get();
            Document doc=  Jsoup.parse(new URL(new_url).openStream(), "gbk", new_url);
            //System.out.println(doc.toString());
            int num=0;
            if(new_url.length()>74){
                elements = doc.getElementsByClass("villagetr");
                elements = elements.select("td");
                // System.out.println(elements.size());
                num=3;
            }
            else {
                elements = doc.select("a");
                num=2;
                //System.out.println(elements.size());
            }
            int temp=0;

            for(Element element :elements){

                if(element.text().contains("京ICP备05034670号")){
                    continue;
                }
                // System.out.println(element.text() + ",");
                temp++;
                if(num==2&&temp!=2){
                    stringBuilder.append(element.text()).append(",").append("\t");
                }
                else{
                    stringBuilder.append(element.text()).append(",");
                }
                if(temp ==num){
                    temp=0;
                    // System.out.println();
                    stringBuilder.append("\r\n");
                }
            }
            urlSet.add(new_url);
            //  Elements links = doc.select("table tr  a");
            Elements links =doc.select("a[href]");
            // System.out.println(links);
            for (Element link : links) {
                String linkHref = link.attr("href");
                //System.out.println(linkHref);
                if (linkHref.equals("#")) {
                    return;
                }
                Matcher matcher = p.matcher(linkHref);
                if (matcher.matches()) {
                    spiderInternet(linkHref, "");
                } else {
                    spiderInternet(new_url, linkHref);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        try {
            // System.out.println(stringBuilder.toString());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("DataNew.csv",true),"utf-8"));
            writer.write(stringBuilder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
