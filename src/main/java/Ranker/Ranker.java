package Ranker;

import java.util.*;

import Indexer.UrlData;
import Indexer.WordData;
import Indexer.SearchWord;
import Ranker.pageRanker;
public class Ranker {

	//Array_of_words got from query processor
	public List<SearchWord> words;
	//Total number of documents
    int noOfDocs;
    // Inverse Document Frequency for each word
    double [] IDF;

    // weights of tags
    public static final double titleWeight= 15;
    public static final double h1Weight = 6;
    public static final double h2Weight = 5;
    public static final double h3Weight = 4;
    public static final double h4Weight = 3;
    public static final double h5Weight = 2;
    public static final double h6Weight = 1.5;
    public static final double bodyWeight= 1;

    // map of<url,tf_IDF>
    public Map<String, Double> tf_IDF= new HashMap<String,Double>();
    public Map<String, UrlData> popularityMap= new HashMap<String,UrlData>();



    public Ranker(int docs,List<SearchWord> w) {
    	noOfDocs=docs;
    	words=w;
    	IDF=new double[words.size()];
		calRelevance();
    }

//	// check spam for each document
//	public boolean checkNotSpam(int word,int doc) {
//		if(words[word].data[doc].count/words[word].data[doc].lengthOfDoc<=0.5)
//		{
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//
//	}
	// it is calculated one time for each word.

	public void calculateIDF(int word)
	{
		IDF[word]=(double)noOfDocs/(words.get(word).df);
	}

	// calculate Relevance
	public void calRelevance() {
		for (int i=0;i<words.size();i++)
		{
			calculateIDF(i);
			for(int j=0;j<words.get(i).data.size();j++)
			{
					if(tf_IDF.get(words.get(i).data.get(j).url) != null)
					{
						double previous=tf_IDF.get(words.get(i).data.get(j).url);
						tf_IDF.put(words.get(i).data.get(j).url,(calTFvalue(i,j)*IDF[i]*calculatePositionsWeight(i,j))+previous);
					}
					else
					{
						tf_IDF.put(words.get(i).data.get(j).url,calTFvalue(i,j)*IDF[i]*calculatePositionsWeight(i,j));
					}
                    UrlData currentData = new UrlData();
                    currentData.FilePath = words.get(i).data.get(j).filepath;
					currentData.popularity = words.get(i).data.get(j).popularity;
                    popularityMap.put(words.get(i).data.get(j).url , currentData);
			}
		}
	}

	public double calTFvalue(int word,int doc)
	{
		double tfValue=(double)words.get(word).data.get(doc).count/words.get(word).data.get(doc).lengthOfDoc;
		return tfValue;
	}


	// Return weight a specific word in a specific doc
	public double calculatePositionsWeight(int word,int doc)
	{
		double totalWeight= 0;
		totalWeight+=(words.get(word).data.get(doc).position.get("title")==null)?0:words.get(word).data.get(doc).position.get("title")*titleWeight;
		totalWeight+=(words.get(word).data.get(doc).position.get("h1")==null)?0:words.get(word).data.get(doc).position.get("h1")*h1Weight;
		totalWeight+=(words.get(word).data.get(doc).position.get("h2")==null)?0:words.get(word).data.get(doc).position.get("h2")*h2Weight;
		totalWeight+=(words.get(word).data.get(doc).position.get("h3")==null)?0:words.get(word).data.get(doc).position.get("h3")*h3Weight;
		totalWeight+=(words.get(word).data.get(doc).position.get("h4")==null)?0:words.get(word).data.get(doc).position.get("h4")*h4Weight;
		totalWeight+=(words.get(word).data.get(doc).position.get("h5")==null)?0:words.get(word).data.get(doc).position.get("h5")*h5Weight;
		totalWeight+=(words.get(word).data.get(doc).position.get("h6")==null)?0:words.get(word).data.get(doc).position.get("h6")*h6Weight;
		totalWeight+=(words.get(word).data.get(doc).position.get("body")==null)?0:words.get(word).data.get(doc).position.get("body")*bodyWeight;
		return totalWeight;
	}


    public LinkedHashMap<UrlData, Double> sortSearched(){

        Map<UrlData, Double> Results= new HashMap<UrlData,Double>();

        for (String url : tf_IDF.keySet())
        {
            UrlData currentData = new UrlData();
            currentData.URL = url;
			currentData.FilePath = popularityMap.get(url).FilePath;
            Results.put(currentData, tf_IDF.get(url)*popularityMap.get(url).popularity);
        }

        //LinkedHashMap preserve the ordering of elements in which they are inserted
        LinkedHashMap<UrlData, Double> reverseSortedMap = new LinkedHashMap<>();

        //Use Comparator.reverseOrder() for reverse ordering
        Results.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        return reverseSortedMap;
    }


	public static void main(String[] args) {

	}

}
