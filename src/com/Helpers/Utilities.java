package com.Helpers;

import com.Models.ParsedSearchResult;
import com.Models.Result;
import com.Models.SearchResult;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivani on 9/26/2015.
 */
public  class Utilities {
    public static List<ParsedSearchResult> mapToParsedSearchResult(SearchResult r)
    {
        List<ParsedSearchResult> parsedSearchResultList = new ArrayList<ParsedSearchResult>();
        List<Result> entry = r.d.results;
        for(int i=0;i<entry.size();i++)
        {
            ParsedSearchResult parsedResult= new ParsedSearchResult();
            parsedResult.description=entry.get(i).Description;
            parsedResult.displayUrl=entry.get(i).DisplayUrl;
            parsedResult.url=entry.get(i).Url;
            parsedResult.id=entry.get(i).ID;
            parsedResult.title=entry.get(i).Title;
            parsedSearchResultList.add(parsedResult);
        }
        return parsedSearchResultList;
    }

    public static  List<ParsedSearchResult> getBingResult(List<String> inp,String BingAPIKey) throws IOException {
        List<ParsedSearchResult> parsedResult = null;
        String query = "%27";
        int i =0;
        for (i = 0; i < inp.size()-1; i++)
            query += inp.get(i) + "%20";
        if(i<inp.size())
            query+=inp.get(i)+"%27";

        String bingUrl = Constants.BingUrl + query + "&$top=10&$format=JSON";


        byte[] accountKeyBytes = Base64.encodeBase64((BingAPIKey + ":" + BingAPIKey).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);
        URL url = new URL(bingUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
        InputStream inputStream = (InputStream) urlConnection.getContent();
        byte[] contentRaw = new byte[urlConnection.getContentLength()];
        inputStream.read(contentRaw);
        String content = new String(contentRaw);
        SearchResult result = new Gson().fromJson(content, SearchResult.class);
        parsedResult = Utilities.mapToParsedSearchResult(result);

        return parsedResult;
    }
}
