package com.sainsbury.challenge;

import com.sainsbury.challenge.pojo.Product;
import com.sainsbury.challenge.pojo.Results;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {

    /**
     * Parses the given html and returns a populated Product POJO
     * @param html
     * @return
     */
    public Product parseProductPage(String html){
        Product product = new Product();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        product.setSize(df.format(html.length()/1024.)+"kb");
        {
            Pattern p = Pattern.compile("<h1>(.*?)</h1>", Pattern.DOTALL);
            Matcher m = p.matcher(html);
            if (m.find()) {
                product.setTitle(m.group(1));
            }
        }
        {
            Pattern p = Pattern.compile("<p class=\"pricePerUnit\">\n *£(.*?)<", Pattern.DOTALL);
            Matcher m = p.matcher(html);
            if (m.find()) {
                product.setUnit_price(Double.parseDouble(m.group(1).trim()));
            }
        }
        {
            String re = "<h3.*?>Description</h3>(.*?)<h3.*?>Nutrition</h3>";
            Pattern p = Pattern.compile(re, Pattern.DOTALL);
            Matcher m = p.matcher(html);
            if (m.find()) {
                String description = m.group(1);
                // remove html tags
                description = description.replaceAll("<.*?>", " ");
                product.setDescription(description.trim());
            }
        }
        return product;
    }

    /**
     * Parses the results page html. For each item found, the url is appended to the resulting list
     * @return
     * @throws IOException
     */
    public List<String> parseResultsPage(String resultsHtml) throws IOException{
        // skip everything after the message of Sponsored items
        String removeAfter = "<h2 class=\"hookLogicTitle\">Sponsored</h2>";
        int idx = resultsHtml.indexOf(removeAfter);
        if (idx != -1){
            resultsHtml = resultsHtml.substring(0, idx);
        }

        List<String> urls = new ArrayList<String>();

        for(String item: resultsHtml.split("<div class=\"productInfo\">")){
            Pattern p = Pattern.compile("<h3>.*?<a href=\"(.*?)\" >",
                    Pattern.DOTALL);
            Matcher m = p.matcher(item);
            if (m.find()) {
                String url = m.group(1);
                if (url.startsWith("//")){
                    url = "https:"+url;
                }
                urls.add(url);
            }
        }

        return urls;
    }

    private void parseResultsAndDetails() throws IOException{
        final String URL = "https://www.sainsburys.co.uk/webapp/wcs/stores/servlet/CategoryDisplay?listView=true&orderBy=FAVOURITES_FIRST&parent_category_rn=12518&top_category=12518&langId=44&beginIndex=0&pageSize=20&catalogId=10137&searchTerm=&categoryId=185749&listId=&storeId=10151&promotionId=#langId=44&storeId=10151&catalogId=10137&categoryId=185749&parent_category_rn=12518&top_category=12518&pageSize=20&orderBy=FAVOURITES_FIRST&searchTerm=&beginIndex=0&hideFilters=true";

        Helper helper = new Helper();
        Results results = new Results();

        String resultsHtml = helper.httpClient(URL);
        for(String url: parseResultsPage(resultsHtml)){
            String detailHtml = helper.httpClient(url);
            Product product = parseProductPage(detailHtml);
            results.setTotal(results.getTotal()+product.getUnit_price());
            results.getResults().add(product);
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(results);
        System.out.println(json);
    }


    public static void main(String[] args) throws IOException{
        Solution solution = new Solution();
        solution.parseResultsAndDetails();
    }
}
