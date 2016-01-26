# Information-Retrival-System-using-Relevance-Feedback

COMS E6111 Advanced Database Systems

Fall 2015 Project 1

Relevance Feedback & Query Expansion

Team members

Shivani Gupta (uni : sg3296)
Siddharth Aman Varshney (uni : sav2125)

Clear description of the internal design of our project :-

Our JAVA code consists of following :-
It consists of 3 packages which are :-
->Helpers
->Models
->queryexpansion

Helpers package consists of the following classes :-
->Constants :- We are applying Rocchio Algorithm. We have taken separate beta, gamma values for description and title. We feel that if a document is relevant, it's title should be given more importance than the description because it is a good indicator of the content on the page. After running several combinations of constants, we found the following constant values to be good parameters for rocchio algorithm.

    beta=0.5;
    gamma=-0.15;
    titleBeta=0.85;
    titleGamma=0;
 
->Porter :- We have used the Porter stemmer for stemming the query and description of web pages. We used this because many times words have the same root. Thus they should be considered equal while term frequency analysis.
->RocchioAlgo :- This class contains the main algorithm for query expansion. This is explained in detail in the next section.
->Utilities :- This class consists of methods which are used for parsing the data fetched from the bing api into our data structures for processing.

Models package consists of the following classes :-

->Metadata 
->ParsedSearchResult
->Result
->Root
->SearchResult
->WordPair
The above 6 classes specify the data structures which are used for parsing the data fetched from the bing api into our data structures for further processing.

->StopWords :- This class is used for removing stop words from the description. This consists of a list of stopwords which are removed from the description and title before processing.


queryexpansion package consists of the following class :-
ExpandQuery :- This class contains the running logic of the whole project. It receives input query, interacts with bing api. After result is received one by one, user gives relevance judgement on each of the results. User's decisions are recorded in the data structure and then rocchio algorithm is applied. After processing, 2 query terms are added to modify query. The program runs until desired precision is achieved.

e) Detailed description of our query-modification method (RocchioAlgo)

{Q_m} =& (df(q_0,D_{jd}). \beta_{description}.\frac{1}{|D_{rd}|}\sum_{D_{jd} \in D_{rd}} {D_{jd}})\\
			&+(df(q_0,D_{jt})\beta_{title}.\frac{1}{|D_{rt}|}\sum_{D_{jt} \in D_{rt}} {D_{jt}})\\
			& - (\gamma.\frac{1}{|D_nr|}.\sum_{D_k \in D_{nr}} {D_k})

* Please refer to the FormulaUsed.jpg file attached for the formula

-> We have not used alpha in our implementation because we only add new query terms to formulate the query. After removing stop words and stemming, the rocchio algorithm computes a score for every term in 
1. description
2. title
-> We used the following formula taken from [1]
                  df(x,y) = log(1+1/D(x,y))
where df(x,y) : logarithmic based distance factor between terms x,y where x is query term and y is probable query expansion term.
D(x,y) : minimum distance between x and y among relevant docs.
This is a modification over the formula as defined in [1].

The final score of all probable terms for query expansion found in description and title is given as :-

            total_score(y) = (relevant_frequency*df(x,y))/D_r + nonrelevant_frequency/D_nr;
            where relevant_frequency : (sum of term frequency in description of all relevant documents)*beta + (sum of                 term frequency in title of all relevant documents)*titlebeta,
            where nonrelevant_frequency : (sum of term frequency in description of all relevant documents)*gamma + (sum of             term frequency in title of all relevant documents)*titlegamma;
 
The top 2 terms with highest total_score are added to query for query expansion in successive order.

References :
[1] O. Vechtomova and Y. Wang, “A Study of the Effect of Term Proximity on Query Expansion,” Inf. Sci., vol. unknow, no. unknow, pp. 1–19, 2006. 
