import java.io.*;
import java.lang.Math;
import java.lang.Integer;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class ranking {

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

/*
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
*/

/*
	// Returns the top n tweets for a given query
	public static String[] TopNTweets(String query, String[] tweets, int n) {
		
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
*/

	// TODO Return index of Tweet JSONs
	// Exact Search
	// Return -1 if not found
	public static int search(String query, String[] topntweetsjsons) {
		int query_rank = -1;
		for (int i = 0; i < topntweetsjsons.length; ++i) {
			if (query.equals(topntweetsjsons[i])) {
				query_rank = i + 1;
				return query_rank;
			}
		}
		return -1;
	}

	// Redo of BM25
	// Recalculate fi
	// n_i and qfi are arrays with the same size of the number of words in the query
	public static double BM25(String query, String tweet, int[] ni, int[] qfi, int N, double avg_doc_length) {
		String[] tweet_terms = tweet.split("\\s");
		String[] query_terms = query.split("\\s");
		int i,j;
		double doc_length = tweet.length();	// Length of the entire tweet (NOT the number of words)
		double x,y,z,fi;
		double score = 0.0;
		double k1 = 1.2; double k2 = 100; double b = 0.75;
		double K = k1 * ( (1.0 - b) + b * (doc_length / avg_doc_length));

		for (i = 0; i < query_terms.length; ++i) {
			fi = 0;		// fi is the number of times the query term appears in the tweet
			for (j = 0; j < tweet_terms.length; ++j) {
				if (query_terms[i].equals(tweet_terms[j])) ++fi;
			}

			System.out.println("fi: " + fi);
			System.out.println("ni: " + ni[i]);
			System.out.println("K: " + K);
			System.out.println("qfi: " + qfi[i]);
			System.out.println("N: " + N);

			System.out.println( (N-ni[i]+0.5)/(ni[i]+0.5));

			x = Math.log10( (N - ni[i] + 0.5) / (ni[i] + 0.5) );
			y = ((k1 + 1) * fi) / (K + fi);
			z = ((k2 + 1) * qfi[i]) / (k2 + qfi[i]);
			System.out.println("x: " + x);
			System.out.println("y: " + y);
			System.out.println("z: " + z);
			score += (x*y*z);
			System.out.println(score);
		}
		System.out.println("OVerall Score: " + score);
		return score;
	}

	// Returns the top n tweet JSONs
	public static String[] toptweets(int n, List<String> jsonsList, double[] scoresList) {
		double max;
		double prevMax = 1000000;
		String[] topnjsons = new String[n];
		int i,j,ind;
		for (i = 0; i < n; ++i) {
			max = -1.0;
			ind = 0;
			for (j = 0; j < scoresList.length; ++j) {
				if (max < scoresList[j] && scoresList[j] < prevMax) {	// Don't get previous maxes
					max = scoresList[j];
					ind = j;		// Index of largest score
				}
			}
			topnjsons[i] = jsonsList.get(ind);	// Get the corresponding json with the largest score
			prevMax = max;
		}
		return topnjsons;	
	}

	public static double[] sort_score(double[] arr) {
		double min,tmp;
		int i,j,ind;
		for (i = 0; i < arr.length; ++i) {
			min = arr[i]; ind = i;
			for (j = i + 1; j < arr.length; ++j) {
				if (min > arr[j]) {
					min = arr[j]; ind = j;
				}
			}
			if (ind != i) {
				tmp = arr[i]; arr[i] = arr[ind]; arr[ind] = tmp;
			}
		}
		return arr;	
	}
	public static void main(String args[]) throws FileNotFoundException,IOException {
		// File input
		// Store contents of file in str
		File f = new File("test.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String st;
		
		
		//Term	{[Name],[ScreenName],[Location],[Hashtag],[Content],[Profile_img_url],[wordcount],...}	
		//st will be a single line. Split via tabs to get the term

		List<String> allkeyvalues= new ArrayList<String>();		// No add or pushback for arrays
		while ((st = br.readLine()) != null) {
			allkeyvalues.add(st);
		}
		// TODO Split allkeyvalues by newline? Whatever separates the key-value pairs
		int N = allkeyvalues.size();	// Should count num of newlines
		System.out.println("N is " + N);

		// Get query from user
		System.out.println("Enter your query: ");
		Scanner user_input = new Scanner(System.in);
		String user_query = user_input.nextLine();
		String[] query_terms = user_query.split("\\s");
		int[] n_i = new int[query_terms.length];


		int qfi,ni,fi;
		int[] qfi_all = new int[query_terms.length];
		int i,j,k,kk;


		// TODO Pattern changed
		String tjson = "\\{([^\\}]*)\\}";
		String pattern = "\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\],\\[([^\\]]*)\\]";
		Pattern tj = Pattern.compile(tjson);
		Matcher tjm;
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		
		List<String> entirejsons = new ArrayList<String>();	// List to hold all individual jsons
		List<String> allContents = new ArrayList<String>();	// Holds all the tweet text of the above jsons

		int sz = 0;
		String jason,content;
		content = jason ="abc";
		boolean dontadd;


		// Calculate n_i values and build the list of tweets/documents
		for (k = 0; k < query_terms.length; ++k) {
			//if the term equals something in allkeyvalues, gt all the tweets and add to a list
			//loop through list later when done. Calculate for each query term

			n_i[k] = 0;	// Initialize the ni for the this term as 0

			for (i = 0; i < allkeyvalues.size(); ++i) {
				// TODO Split off something else
				String[] keyvalue = allkeyvalues.get(i).split("\\t");
				System.out.print("Splitting the key value pair:\n\t" + keyvalue[0] + "\n\t" + keyvalue[1] + "\n");


				// if this term exists in any of the tweets. Should only happen once per query term
				// if it doesn't exist, n_i[k] = 0
				if (query_terms[k].equals(keyvalue[0])) {
					sz = 0;
					tjm = tj.matcher(keyvalue[1]);		// Split into individual jsons
					while(tjm.find()) {
						++sz;
						jason = tjm.group(1);
						m = p.matcher(jason);		// Break json into 7 parts
						System.out.println(jason);
						while(m.find()) content = m.group(5);		// TODO
						System.out.println("Content: " + content);
						if (allContents.size() == 0) {
							allContents.add(content);
							entirejsons.add(jason);
						}
						else {
							dontadd = false;
							for (kk = 0; kk < allContents.size();++kk) {
								// If this tweet exists in our jsons already, don't add it
								if (content.equals(allContents.get(kk))) {
									System.out.println("Found the same tweet");
									dontadd = true;
									kk = allContents.size();
								}
							}
							// Add tweet text to allContents
							// Add entire tweet JSON to entirejsons
							if (dontadd == false) {
								//System.out.println("Adding tweet to allContents");
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
		System.out.println("Sanity Check");
		for (String rgn: allContents) System.out.println(rgn);
		System.out.println("Done with sanity check");
		

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
		double[] scoresList = new double[entirejsons.size()];	
	
		for (i = 0; i < entirejsons.size(); ++i) {
			scoresList[i] = BM25(user_query, allContents.get(i), n_i, qfi_all, N, avg_doc_length);
			System.out.println(scoresList[i]);
		}

		
		// Ranking time		
		int n = 2;	// TODO Update this to 100 later
		String[] topnjsons = toptweets(n,entirejsons,scoresList);	// Store top n tweet jsons in topnjsons

		System.out.println("Size of topnjsons: " + topnjsons.length);

		for (String yyy:topnjsons) System.out.println(yyy);
		System.out.println("------");

		scoresList = sort_score(scoresList);
		for (i = 0; i < topnjsons.length; ++i) {
			System.out.println(topnjsons[i]);
			System.out.println("\tScore: " + scoresList[i]);
		}
	}
}
