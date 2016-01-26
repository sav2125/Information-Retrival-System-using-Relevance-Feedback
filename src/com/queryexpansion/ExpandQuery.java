package com.queryexpansion;
import com.Helpers.RocchioAlgo;
import com.Helpers.Utilities;
import com.Models.ParsedSearchResult;
import com.Models.WordPair;

import java.io.IOException;
import java.util.*;
public class ExpandQuery {
    public static void main(String args[]) throws IOException {
        List<String> input = new ArrayList<String>();
        List<WordPair> top2Words = new ArrayList<WordPair>();
        String query = "";
        float initialPrecision10 = 0;
        float precision_old=0;
        float precision10 = 0;
        int feedback = 0;
        String BingApiKey;


        //Validation on format of arguments passed
        if (args.length < 3) {
            System.out.println("Not enough parameters specified. Please specidy the parameters in the following order:");
            System.out.println("BingApiKey @precision10 query");
            return;
        }

        BingApiKey=args[0];

        try {
            initialPrecision10 = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("For precision@10 value please specify a number.");
            return;
        }

        //after the bing api key and precision take remaining values as query params
        for (int queryParams = 2; queryParams < args.length; queryParams++) {
            input.add(args[queryParams]);
            query = query + " " +args[queryParams]+ " ";
        }

        int iter=0;

        //looping till precision10 is less then user defined precision
        while (precision10 < initialPrecision10) {

            iter++;

            //Not needed to print first time, print only after first feedback
            if (feedback == 1) {
                System.out.println("FeedbackSummary:");
                System.out.println("Query      = " + query);
                System.out.println("Precision  = " + initialPrecision10);
                System.out.println("Still below the expected precision");
                System.out.println("Augmenting query by:" + top2Words.get(0).original_word + " " + top2Words.get(1).original_word);

                input.add(top2Words.get(0).original_word);
                input.add(top2Words.get(1).original_word);
            }

            //Print all parameters
            System.out.println("Parameters:");
            System.out.println("Client Key = " + BingApiKey);
            System.out.println("Query      = " + query);
            System.out.println("Precision  = " + precision10);
            precision_old=precision10/10;
            precision10 = 0;


            //Get bing results and parse to get ParsedSearchResult
            List<ParsedSearchResult> result = Utilities.getBingResult(input,BingApiKey);
            int count = 1;

            RocchioAlgo algo= new RocchioAlgo(input);

            //Take user input for each result and parse through the algo
            for (ParsedSearchResult r : result) {
                System.out.println("Result " + count);
                count++;
                System.out.println(r);
                System.out.println();
                System.out.println("Relevant (Y/N)?");
                Scanner inp = new Scanner(System.in);
                String in = inp.nextLine();
                String yes = "y";
                String no = "n";
                if (yes.equals(in.toLowerCase())) {
                    precision10++;
                    algo.compute(true, r,input);
                } else
                    algo.compute( false, r,input);
            }

            //Parse the word_frequency hash map to get the total score based on relevant and non-relevant frequency
            Iterator it = algo.word_frequency.entrySet().iterator();
            top2Words = new ArrayList<WordPair>();
            Map.Entry pair;
            WordPair obj;
            while (it.hasNext()) {
                pair = (Map.Entry) it.next();
                obj = (WordPair) pair.getValue();

                //compute total score based on relevant and non relevant frequency as well as the distance
                obj.totalScore = (Math.log(1 +(1/ obj.distance))*obj.relevantFrequency) / precision10 + obj.nonRelevantFrequency / (10 - precision10);
                if (input.contains(obj.original_word))
                    continue;
                obj.original_word=obj.original_word.replaceAll("\\s+","");
                if (top2Words.isEmpty()) {
                    pair = (Map.Entry) it.next();
                    obj = (WordPair) pair.getValue();
                    top2Words.add(obj);
                } else if (top2Words.size() == 1) {
                    pair = (Map.Entry) it.next();
                    obj = (WordPair) pair.getValue();
                    if (obj.totalScore > top2Words.get(0).totalScore)
                        top2Words.add(0, obj);
                    else
                        top2Words.add(obj);
                } else if (obj.totalScore > top2Words.get(0).totalScore) {
                    top2Words.add(0, obj);
                    top2Words.remove(2);
                } else if (obj.totalScore > top2Words.get(1).totalScore) {
                    top2Words.add(1, obj);
                    top2Words.remove(2);
                }
            }
            precision10 /= 10;
            if(precision10 < initialPrecision10)
                query = query + top2Words.get(0).original_word + " " + top2Words.get(1).original_word + " ";

            feedback = 1;

            if(iter==1 && precision10==0)
                break;
        }

        System.out.println("FeedbackSummary:");
        System.out.println("Query      = " + query);
        System.out.println("Precision  = " + precision10);
        System.out.println("Desired precision reached.");

    }


}
