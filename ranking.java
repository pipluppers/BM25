import java.io.*;
import java.lang.Math;
import java.util.Scanner;

public class ranking {

	public static double avgdl(String[] allTweets) {
		double total_words = 0.0;
		String[] tweet_words;
		for (String s:allTweets) {
			tweet_words = s.split("\\s");
			total_words += tweet_words.length;
		}
		return total_words / allTweets.length;
	}

	public static double tweet_num(String[] allTweets, String query_term) {
		double ni = 0;
		for (String s:allTweets) {
			if (s.equals(query_term)) ni++;
		}
		return ni; 
	}

	public static double BM25(String Query, String Tweet, String[] allTweets, double avg_doc_length) {
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

	public static void main(String args[]) {
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
		Scanner user_input = new Scanner(System.in);
		String user_query = "";
		user_query += user_input.nextLine();
		System.out.println("User query: " + user_query);

		int x = search(user_query, topnTweets);
		if (x == -1) System.out.println("User query not found in top " + n + " tweets");
		else System.out.println("Found user query at rank " + x);

	}

}
