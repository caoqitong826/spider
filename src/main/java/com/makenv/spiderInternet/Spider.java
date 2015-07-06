package com.makenv.spiderInternet;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * Created by caoqitong on 3/26/2015.
 */
public class Spider {
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
        String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2013/14/1401.html";
        spiderInternet(baseUrl, "");
    }

    private static void spiderInternet(String baseUrl, String exUrl) {
        if (baseUrl.endsWith(".html") && exUrl!="") {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/')+1);
        }
//        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));
//        System.out.println(baseUrl);
        String new_url = baseUrl + exUrl;
        //System.out.println(new_url);
        if (urlSet.contains(new_url)) {
            return;
        }
        //System.out.println(new_url);
        Elements elements=null;
        try {
            Document doc = Jsoup.connect(new_url).get();

            elements = doc.select("table table tr td a");

            //System.out.println(doc.select("td a[href]"));
           // System.out.print(elements);
            for(Element element :elements){
                if(element.text()=="京ICP备05034670号"){
                   continue;
                }
                System.out.println(element.text());
            }
            urlSet.add(new_url);
            Elements links = doc.select("table tr td a");
            for (Element link : links) {
                String linkHref = link.attr("href");
                //System.out.println(linkHref);
                if (linkHref.equals("#")) {
                    return;
                }
                Matcher matcher = p.matcher(linkHref);
               // System.out.println(matcher.matches());
                if (matcher.matches()) {
                    spiderInternet(linkHref, "");
                } else {
                    spiderInternet(baseUrl, linkHref);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }


}
