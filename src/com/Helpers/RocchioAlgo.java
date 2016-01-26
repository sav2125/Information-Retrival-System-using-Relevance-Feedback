package com.Helpers;

import com.Models.ParsedSearchResult;
import com.Models.StopWords;
import com.Models.WordPair;

import java.util.*;

/**
 * Created by shivani on 10/5/2015.
 */
public class RocchioAlgo {
    public float alpha;
    public double beta;
    public double gamma;
    public double titleBeta;
    public double titleGamma;
    public HashMap<String, WordPair> word_frequency;
    HashMap queryTermIndexes;
    HashMap queryTermTitleIndexes;

    public RocchioAlgo(List<String> input)
    {
        alpha=Constants.alpha;
        beta=Constants.beta;
        gamma=Constants.gamma;
        titleBeta=Constants.titleBeta;
        titleGamma=Constants.titleGamma;
        word_frequency=new HashMap();
        queryTermIndexes=new HashMap<String,List<Integer>>();
        queryTermTitleIndexes=new HashMap<String,List<Integer>>();
    }


     public void compute( boolean relevant,ParsedSearchResult result,List<String> input) {
         queryTermIndexes=new HashMap<String,List<Integer>>();
         queryTermTitleIndexes=new HashMap();
         Porter port = new Porter();
         StopWords stopWords = new StopWords();

         String delims = "[ .,?!(){}-]";

         ArrayList<String> terms = new ArrayList<String>(Arrays.asList(result.description.split(delims)));
         ArrayList<String> termsTitle = new ArrayList<String>(Arrays.asList(result.title.split(delims)));

         for (int i = 0; i < terms.size(); i++) {
             terms.set(i, terms.get(i).toLowerCase());
         }

         for (int i = 0; i < terms.size(); i++) {
             result.words = stopWords.RemoveWords(terms);
         }
         for (int i = 0; i < termsTitle.size(); i++) {
             termsTitle.set(i, termsTitle.get(i).toLowerCase());
         }
         for (int i = 0; i < termsTitle.size(); i++) {
             result.titleWords = stopWords.RemoveWords(termsTitle);
         }

         for (int i = 0; i < result.words.size(); i++) {
             String str = terms.get(i);
             String temp = port.stripAffixes(str);
             WordPair term = new WordPair();
             term.original_word = Porter.Clean(str);
             result.words.set(i,temp);

             if (word_frequency.get(temp) == null) {
                 if (relevant)
                     term.relevantFrequency = beta;
                 else
                     term.nonRelevantFrequency = gamma;
                 word_frequency.put(temp, term);
             } else {
                 WordPair wp = (WordPair) word_frequency.get(temp);
                 if (relevant)
                     wp.relevantFrequency += beta;
                 else
                     wp.nonRelevantFrequency += gamma;
                 word_frequency.put(temp, wp);
             }
         }

         hashMapInitialize(input,result.words);
         for(int i=0;i<result.words.size();i++)
         {
             if(relevant) {
                 double distance = getDistance(i);
                 WordPair pair = word_frequency.get(result.words.get(i));
                 if (pair.distance > distance)
                     word_frequency.get(result.words.get(i)).distance = distance;
             }
         }

         for (int i = 0; i < result.titleWords.size(); i++) {
             String str = termsTitle.get(i);
             String temp = port.stripAffixes(str);
             WordPair term = new WordPair();
             term.original_word = Porter.Clean(str);
             result.titleWords.set(i, temp);
             if (word_frequency.get(temp) == null) {
                 if (relevant)
                     term.relevantFrequency = titleBeta;
                 else
                     term.nonRelevantFrequency = titleGamma;
                 word_frequency.put(temp, term);
             } else {
                 WordPair wp = (WordPair) word_frequency.get(temp);
                 if (relevant)
                     wp.relevantFrequency += titleBeta;
                 else
                     wp.nonRelevantFrequency += titleGamma;
                 word_frequency.put(temp, wp);
             }
         }

         titlehashMapInitialize(input,result.titleWords);

         for(int i=0;i<result.titleWords.size();i++)
         {
             if(relevant) {
                 double distance = titleGetDistance(i);
                 WordPair pair = word_frequency.get(result.titleWords.get(i));
                 if (pair.distance > distance)
                     word_frequency.get(result.titleWords.get(i)).distance = distance;
             }
         }

     }

    private void titlehashMapInitialize(List<String> input, ArrayList<String> words) {
        for(int i=0;i<input.size();i++)
        {
            for(int j=0;j<words.size();j++)
            {
                String inp = input.get(i);
                if(inp.equals(words.get(j)))
                {
                    if(queryTermTitleIndexes.containsKey(input.get(i)))
                    {
                        List<Integer> indxs= (List<Integer>)queryTermTitleIndexes.get(input.get(i));
                        indxs.add(j);
                        queryTermTitleIndexes.remove(input.get(i));
                        queryTermTitleIndexes.put(input.get(i),indxs);
                    }
                    else {
                        List<Integer> indxs = new ArrayList<Integer>();
                        indxs.add(j);
                        queryTermTitleIndexes.put(input.get(i), indxs);
                    }

                }
            }
        }
    }

    private void hashMapInitialize(List<String> input, ArrayList<String> words) {
        for(int i=0;i<input.size();i++)
        {
            for(int j=0;j<words.size();j++)
            {
                String inp = input.get(i);
                if(inp.equals(words.get(j)))
                {
                    if(queryTermIndexes.containsKey(input.get(i)))
                    {
                        List<Integer> indxs= (List<Integer>)queryTermIndexes.get(input.get(i));
                        indxs.add(j);
                        queryTermTitleIndexes.remove(input.get(i));
                        queryTermTitleIndexes.put(input.get(i),indxs);
                    }
                    else {
                        List<Integer> indxs = new ArrayList<Integer>();
                        indxs.add(j);
                        queryTermIndexes.put(input.get(i), indxs);
                    }

                }
            }
        }
    }

    private double getDistance(int index)
    {
        Iterator it = queryTermIndexes.entrySet().iterator();
        int distance=100000;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<Integer> indxs = (List<Integer>)pair.getValue();
            for(int i=0;i<indxs.size();i++)
            {
                if(distance>Math.abs(index-indxs.get(i)))
                    distance=Math.abs(index-indxs.get(i));
            }
        }
        return distance;
    }

    private double titleGetDistance(int index)
    {
        Iterator it = queryTermTitleIndexes.entrySet().iterator();
        int distance=100000;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<Integer> indxs = (List<Integer>)pair.getValue();
            for(int i=0;i<indxs.size();i++)
            {
                if(distance>Math.abs(index-indxs.get(i)))
                    distance=Math.abs(index-indxs.get(i));
            }
        }
        return distance;
    }
}