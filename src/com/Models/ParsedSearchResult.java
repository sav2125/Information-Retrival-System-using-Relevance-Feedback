package com.Models;

import java.util.ArrayList;

/**
 * Created by shivani on 9/26/2015.
 */
public class ParsedSearchResult {
    public String url;
    public String displayUrl;
    public String title;
    public String description;
    public String id;
    public int flag;
    public ArrayList<String> words;
    public ArrayList<String> titleWords;

    public String toString()
    {
        return ("[ \n URL:"+url+"\n Title:"+title+"\n Description:"+description+"\n ]");
    }
}
