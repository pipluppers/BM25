import java.io.*;
import java.lang.Math;
import java.lang.Integer;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class ranking {

	// Call TopNTweets(query, alltweets, n). BM25 will happen under this function for all of the tweets

	public static double avgdl(List<String> allTweets) {
		double total_words = 0.0;
//		String[] tweet_words;
		for (String s:allTweets) {
//			tweet_words = s.split("\\s");
//			total_words += tweet_words.length;		// Add number of words in tweet
			total_words += s.length();			// Add size of tweet
		}
		return total_words / allTweets.size();
	}

	// Calculate ni incorrectly (?) ni is # of docs the query term appears in
	public static double tweet_num(String[] allTweets, String query_term) {
		double ni = 0;
		for (String s:allTweets) {
			if (s.equals(query_term)) ni++;
		}
		return ni; 
	}

	public static double BM25(String Query, String Tweet, String[] allTweets, double avg_doc_length) {
//	public static double BM25(String Query, String Tweet, int N, int ni, int fi, double avg_doc_length) {
		double k1 = 1.2;
		double k2 = 100;
		double b = 0.75;

		double ni;	// Number of tweets this query term appears in
		double N = allTweets.length;	// Number of tweets we have
		double fi;	// Number of times the query term appears in tweet
		double qfi;	// Number of times the query term appears in query
		double K;
		double doc_length;

		String[] query_words = Query.split("\\s");
		String[] tweet_words = Tweet.split("\\s");
		// Calculate K
		doc_length = tweet_words.length;
		K = k1 * ((1.0-b) + b * (doc_length / avg_doc_length));

		double score,x,y,z;
		x = y = z = score = 0.0;
		for (String s:query_words) {
			qfi = fi = 0;
			// Calculate qfi and fi
			for (String s2:query_words) if (s.equals(s2)) qfi++;
			for (String s3:tweet_words) if (s.equals(s3)) fi++;
			//System.out.println("Word is " + s + " and qfi = " + qfi + " and fi = " + fi);

			ni = tweet_num(allTweets, s);

			x = Math.log10( (N-ni+0.5)/(ni+0.5) );
			y = ((k1 + 1)*fi) / (K + fi);
			z = ((k2 + 1)*qfi) / (k2 + qfi);
			score += (x*y*z);
		}
		return score;
	}

	// Returns the top n tweets for a given query
	public static String[] TopNTweets(String query, String[] tweets, int n) {
		
		double avg_tweets_l = avgdl(tweets);
		System.out.println("Average tweet length: " + avg_tweets_l);
		

		// Score all tweets
		double[] scoresList = new double[tweets.length];
		int i = 0;
		for (i = 0; i < tweets.length; ++i) {
			// BM25(Query, Tweet, List of all tweets, avg tweet length)
			scoresList[i] = BM25(query, tweets[i], tweets, avg_tweets_l);
			//System.out.println("Tweet Text: " + tweets[i] + "\n\tScore: " + scoresList[i]);
		}

		
		// Rank tweets in order
		double maxScore = 0.0;
		double prevMax = 10000;	// so we don't find the same max score
		double[] topnScores = new double[n];
		String[] topnTweets = new String[n];
		int j = 0;
		int ind = 0;		// Store the index of where we found the max
		for (i = 0; i < n; ++i) {	// Finding top 2
			maxScore = -1.0;
			for (j = 0; j < tweets.length; ++j) {
				if (maxScore < scoresList[j] && scoresList[j] < prevMax) {
					maxScore = scoresList[j];
					ind = j;
				}
			}
			prevMax = maxScore;	// Update previous max so we don't register this as max again
			topnScores[i] = scoresList[ind];
			topnTweets[i] = tweets[ind];
		}

		// Print top n Tweets		
		System.out.println("Top " + n + " tweets:");
		for (i = 0; i < topnScores.length; ++i) {
			System.out.println(i+1 + ": " + topnTweets[i] + "\n\tScore: " + topnScores[i]);
		}
		return topnTweets;
	}

	// TODO Return Tweet JSONs
	public static int search(String query, String[] topntweets) {
		int query_rank = -1;
		for (int i = 0; i < topntweets.length; ++i) {
			if (query.equals(topntweets[i])) {
				query_rank = i + 1;
				return query_rank;
			}
		}
		return -1;
	}

	// Redo of BM25
	// Recalculate fi and qfi
	// n_i is an array with the same size of the number of words in the query
	public static double BM25(String query, String tweet, int[] ni, int[] qfi, int N, double avg_doc_length) {
		String[] tweet = tweet.split("\\s");
		String[] query_terms = query.split("\\s");
		int i,j;
		doc_length = tweet.length();
		double x,y,z;
		double k1 = 1.2;
		double k2 = 100;
		double b = 0.75;
		double K = k1 * ( (1.0 - b) + b * (doc_length / avg_doc_length));
		for (i = 0; i < query_terms.length; ++i) {

			x = Math.log10( (N - ni[i] + 0.5) / (ni[i] + 0.5) );
			y = ((k1 + 1) * fi) / (K + fi);
			z = ((k2 + 1) * qfi[i]) / (k2 + qfi[i]);
		}	
	}

	public static void main(String args[]) throws FileNotFoundException,IOException {
		// File input
		// Store contents of file in str
		File f = new File("test.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String st; String str = "";
		while ((st = br.readLine()) != null) str += st;
		//System.out.println(str);


		
		/*
			Term	{[Name],[ScreenName],[Location],[Hashtag],[Content],[Profile_img_url],[wordcount],...}
		
			st will be a single line. Split via tabs to get the term

		List<String> allkeyvalues= new ArrayList<String>();		// No add or pushback for arrays
		while ((st = br.readLine()) != null) {
			allkeyvalues.add(st);
		}
		int N = allkeyvalues.size();

		// Get query from user
		Scanner user_input = new Scanner(System.in);
		String user_query = user_input.nextLine();
		String[] query_terms = user_query.split("\\s");
		int[] n_i = new int[query_terms.length];

		int qfi,ni,fi;
		int[] qfi_all = new int[query_terms.length];
		int i,j,k,kk;
		String name,screen_name,loc,hashtag,content,prof,str_wordcount;
		// TODO Pattern changed
		String tjson = "{([^}]*)}";
		String pattern = "\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\]";
		Pattern tj = Pattern.compile(tjson);
		Matcher tjm;
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		List<String> entirejsons = new ArrayList<String>();	// List to hold all individual jsons

		List<int> scoresList = new ArrayList<int>();		// List to hold all the scores

		List<String> allTweetJsons = new ArrayList<String>();	// List to hold all tweet jsons that query appears in	
		List<String> allContents = new ArrayList<String>();	// Holds all the contents of the above list

		int sz = 0;
		String jason,content;
		boolean dontadd;
		double score = 0.0;	// Going to add to this at the end of each iteration of the loop

		// Calculate n_i values and build the list of tweets/documents
		for (k = 0; k < query_terms.length; ++k) {
			//if the term equals something in allkeyvalues, gt all the tweets and add to a list
			//loop through list later when done. Calculate for each query term

			n_i[k] = 0;	// Initialize the ni for the this term as 0

			for (String i : query_terms) {
				if (query_terms[k].equals(i)) ++qfi;		// It's not going to be 0
			}

			for (i = 0; i < allkeyvalues.size(); ++i) {
				// TODO Split off something else
				String[] keyvalue = allkeyvalues.get(i).split("\\t");

				// if this term exists in any of the tweets. Should only happen once per query term
				if (term.equals(keyvalue[0])) {
					sz = 0;
					alljsons.clear();				// Empty jsons
					tmj = tj.matcher(keyvalue[1]);		// Split into individual jsons
					while(tjm.find()) {
						++sz;
						jason = tjm.group(1);
						m = p.matcher(jason);		// Break json into 7 parts
						content = m.group(7);
						if (allContents.size() == 0) {
							allContents.add(content);
							entirejsons.add(jason);
						}
						else {
							dontadd = False;
							for (kk = 0; kk < allContents.size();++kk) {
								// If this tweet exists in our jsons already, don't add it
								if (content.equals(allContents.get(k))) {
									dontadd = True;
									break;
								}
							}
							// Add tweet text to allContents
							// Add entire tweet JSON to entirejsons
							if (dontadd == False) {
								allContents.add(content);
								entirejsons.add(jason);
							}
						}
								
					}
					n_i[k] = sz;	// Should contain the n_values for every single query term
					i = allkeyvalues.size();	// Stop the loop early. Term should only match a single key
				}
			}
		}

		for (i = 0; i < query_terms.length; ++i) {
			qfi = 0;
			for (j = 0; j < query_terms.length; ++j) {
				if (query_terms[i].equals(query_terms[j])) ++qfi;
			}
			qfi_all[i] = qfi;
		}
		double avg_doc_length = avgdl(allContents);
		// Loop through the tweets now
		// Have a score for each tweet
		for (i = 0; i < entirejsons.size(); ++i) {
			score = 0.0;

			// Loop through query and calculate the score for each tweet
			for (j = 0; j < query_terms.length; ++j) {
				// BM25 (query, tweet, ni, N) 
				BM25(user_query, allContents.get(j), n_i, qfi_all, N,avg_doc_length);

			}




		}
//	--------------Potentially wrong
					for (j = 0; j < alljsons.size(); ++j) {
						m = p.matcher(alljsons.get(i));	// Split json into 7 parts
						fi = Integer.parseInt(m.group(7));

						scoresList.add(BM25(query_terms[k], ni, fi, qfi, N));
					}			
				}
			}
			score += 
		}

//	---------------------------------------Might Remove Below this line -------------------------------
		// Loop through all key-value pairs
		for (int i = 0; i < allkeyvalues.size(); ++i) {

			String[] keyvaluepair = allkeyvalues.get(i).split("\\t");
			String term = keyvaluepair[0];
			String tweetjson = keyvaluepair[1];

			String name,screen_name,loc,hashtag,content,profile,str_wordcount;
			int wordcount;
			String tjson = "{([^}]*)}";

			// Get list of all Tweet Jsons. Store in list jsons
			List<String> jsons = new ArrayList<String>();	// Arrays don't have an add or push_back
			Pattern tj = Pattern.compile(tjson);
			Matcher tjm = tj.matcher(tweetjson);
			while(tjm.find()) jsons.add(tjm.group(1));
			int ni = jsons.size();			

			//	Copy list to String array
			String[] alltweetjsons = new String[jsons.size()];
			for (int i = 0; i < jsons.size(); ++i) {
				alltweetjsons[i] = jsons.get(i);
			}
		String pattern = "\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\]";

		Pattern p = Pattern.compile(pattern);
		Matcher m;
		// Loop through all jsons.
		for (int i = 0; i < jsons.size(); ++i) {
			m = p.matcher(jsons.get(i));
			while (m.find()) {
				name = m.group(1); screen_name = m.group(2); loc = m.group(3); hashtag = m.group(4); content = m.group(5);
				profile = m.group(6); wordcount = Integer.parseInt(m.group(7));
			}

		// Get query from user
		// TopNTweets(query, tweets, 
 		*/



		// Test query and tweets. Works
		String query = "Hello, my name is Alex";
		String a = "Job pop my is Hello,";
		String b = "Piplup Alex Alex Tim";
		String c = "Mango Honeydew Strawberry Chocolate";
		String d = "Poppy Seeds is adorable";
		int n = 4;		// TODO Update this to 100 later to get top 100 tweets
		String[] tweets = new String[n];
		tweets[0] = a; tweets[1] = b; tweets[2] = c; tweets[3] = d;

		// Top n Tweets
		String[] topnTweets;
		topnTweets = TopNTweets(query, tweets, n);

		for (int i = 0; i < topnTweets.length; ++i) {
			System.out.println(i+1 + ": " + topnTweets[i]);
		}

		// Get a single line of input from the user
		System.out.print("Enter query: ");
		Scanner user_input = new Scanner(System.in);
		String user_query = "";
		user_query += user_input.nextLine();
		System.out.println("User query: " + user_query);

		int x = search(user_query, topnTweets);
		if (x == -1) System.out.println("User query not found in top " + n + " tweets");
		else System.out.println("Found user query at rank " + x);

	}
}
