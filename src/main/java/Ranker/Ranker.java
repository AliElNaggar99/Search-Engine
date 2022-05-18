/*
package Ranker;

import java.util.HashMap;
import java.util.Map;
import Indexer.WordData;
import Indexer.SearchWord;
import java.util.LinkedHashMap;
import java.util.Comparator;
import Ranker.pageRanker;
public class Ranker {
	
	//Array_of_words got from query processor 
	public SearchWords [] words;
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

    public Ranker(int docs,SearchWords [] w) {
    	noOfDocs=docs;
    	words=w;
    	IDF=new double[words.length];
		calRelevance();
    }

//	// check spam for each document
	public boolean checkNotSpam(int word,int doc) {
		if(words[word].data[doc].count/words[word].data[doc].lengthOfDoc<=0.5)
		{
			return true;
		}
		else 
		{
			return false;
		}
		
	}
	// it is calculated one time for each word.

	public void calculateIDF(int word)
	{
		

		IDF[word]=(double)noOfDocs/(words[word].df);

	}
	
	// calculate Relevance
	public void calRelevance() {
		
		for (int i=0;i<words.length;i++) 
		{
			calculateIDF(i);
			for(int j=0;j<words[i].data.length;j++) 
			{
				
				if(checkNotSpam(i,j))
				{
					System.out.println("IDF " + IDF[i]);
					System.out.println("pos " +calculatePositionsWeight(i,j));
					System.out.println("tf " +calTFvalue(i,j));
					if(tf_IDF.get(words[i].data[j].url) != null)
					{
						double previous=tf_IDF.get(words[i].data[j].url);
						System.out.println("pre " + previous);
						tf_IDF.put(words[i].data[j].url,(calTFvalue(i,j)*IDF[i]*calculatePositionsWeight(i,j))+previous);
					}
					else
					{
						tf_IDF.put(words[i].data[j].url,calTFvalue(i,j)*IDF[i]*calculatePositionsWeight(i,j));
	
					}
				}

				
			}
		}
		System.out.println("tf_IDF " + tf_IDF);
	}

	public double calTFvalue(int word,int doc) 
	{
		double tfValue=(double)words[word].data[doc].count/words[word].data[doc].lengthOfDoc;
		System.out.println("count"+words[word].data[doc].count+" len"+words[word].data[doc].lengthOfDoc);
		return tfValue;
	}
	

	// Return weight a specific word in a specific doc
	public double calculatePositionsWeight(int word,int doc) 
	{
		double totalWeight= 0;
		totalWeight+=(words[word].data[doc].position.get("title")==null)?0:words[word].data[doc].position.get("title")*titleWeight;
		totalWeight+=(words[word].data[doc].position.get("h1")==null)?0:words[word].data[doc].position.get("h1")*h1Weight;
		totalWeight+=(words[word].data[doc].position.get("h2")==null)?0:words[word].data[doc].position.get("h2")*h2Weight;
		totalWeight+=(words[word].data[doc].position.get("h3")==null)?0:words[word].data[doc].position.get("h3")*h3Weight;
		totalWeight+=(words[word].data[doc].position.get("h4")==null)?0:words[word].data[doc].position.get("h4")*h4Weight;
		totalWeight+=(words[word].data[doc].position.get("h5")==null)?0:words[word].data[doc].position.get("h5")*h5Weight;
		totalWeight+=(words[word].data[doc].position.get("h6")==null)?0:words[word].data[doc].position.get("h6")*h6Weight;
		totalWeight+=(words[word].data[doc].position.get("body")==null)?0:words[word].data[doc].position.get("body")*bodyWeight;
		
		return totalWeight;
	}
	
	
	public static void main(String[] args) {
		int numOfdocuments=5;
		SearchWords []word= new SearchWords [2];
		word[0]=new SearchWords();
		word[1]=new SearchWords();
		
		word[0].df=1;
		word[1].df=4;

		word[0].data=new WordData[word[0].df];
		word[1].data=new WordData[word[1].df];

		word[0].data[0]=new WordData();
		word[0].data[0].count=2;
		word[0].data[0].lengthOfDoc=30;
		word[0].data[0].position.put("title",1);
		word[0].data[0].position.put("h6",1);
		word[0].data[0].url="url1";
		
		word[1].data[0]=new WordData();
		word[1].data[0].count=4;
		word[1].data[0].lengthOfDoc=30;
		word[1].data[0].position.put("body",4);
		word[1].data[0].url="url1";
		
		word[1].data[1]=new WordData();
		word[1].data[1].count=2;
		word[1].data[1].lengthOfDoc=20;
		word[1].data[1].position.put("h5",2);
		word[1].data[1].url="url2";
		
		word[1].data[2]=new WordData();
		word[1].data[2].count=3;
		word[1].data[2].lengthOfDoc=50;
		word[1].data[2].position.put("h5",2);
		word[1].data[2].position.put("h3",1);
		word[1].data[2].url="url3";
		
		word[1].data[3]=new WordData();
		word[1].data[3].count=5;
		word[1].data[3].lengthOfDoc=50;
		word[1].data[3].position.put("body",5);
		word[1].data[3].url="url4";





		Map<String, Double> Results= new HashMap<String,Double>();

		Ranker r1=new Ranker(numOfdocuments,word);

		pageRanker pRank= new pageRanker(100,0.85);
		System.out.println("popularity " + pRank.popularity);

		for (String url : r1.tf_IDF.keySet())
		{
			Results.put(url, r1.tf_IDF.get(url)*pRank.popularity.get(url));
			//LinkedHashMap preserve the ordering of elements in which they are inserted

		}
		LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();

//Use Comparator.reverseOrder() for reverse ordering
		Results.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

	}

}
*/
