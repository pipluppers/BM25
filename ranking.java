import java.io.*;
import java.lang.Math;
import java.lang.Integer;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class ranking {

	// HOW TO RUN
	// javac ranking.java
	// java ranking test2.txt

	public static double avgdl(List<String> allTweets) {
		double total_words = 0.0;
		for (String s:allTweets) total_words += s.length();			// Add size of tweet
		return total_words / allTweets.size();
	}

	// TODO Return index of Tweet JSONs
	// Exact Search
	// Return -1 if not found
	public static int search(String query, String[] topntweetsjsons) {
		String pattern = "\\[\"name\" : ([^\\]]*), \"screen name\" : ([^\\]]*), \"location\" : ([^\\]]*), \"content\" : \"([^\\]]*)\", \"profile image url\" : ([^\\]]*), \"frequency in tweet\" : (\\d+)\\]";
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		for (int i = 0; i < topntweetsjsons.length; ++i) {
			m = p.matcher(topntweetsjsons[i]);
			while(m.find()) {
				if (query.equals(m.group(4))) return i + 1;
			}
		}
		return -1;
	}

	// n_i and qfi are arrays with the same size of the number of words in the query
	public static double BM25(List<String> unique_query_terms, String tweet, int[] ni, int[] qfi, int N, double avg_doc_length) {
		String[] tweet_terms = tweet.split("\\s");
		int i,j;
		double doc_length = tweet.length();	// Length of the entire tweet (NOT the number of words)
		double x,y,z,fi;
		double score = 0.0;
		double k1 = 1.2; double k2 = 100; double b = 0.75;
		double K = k1 * ( (1.0 - b) + b * (doc_length / avg_doc_length));

		for (i = 0; i < unique_query_terms.size(); ++i) {
			fi = 0;		// fi is the number of times the query term appears in the tweet
			for (j = 0; j < tweet_terms.length; ++j) {
				if (unique_query_terms.get(i).equals(tweet_terms[j])) ++fi;
			}
			x = Math.log10( (N - ni[i] + 0.5) / (ni[i] + 0.5) );
			y = ((k1 + 1) * fi) / (K + fi);
			z = ((k2 + 1) * qfi[i]) / (k2 + qfi[i]);
			score += (x*y*z);
		}
		return score;
	}

	// Returns the top n tweet JSONs
	public static String[] toptweets(int n, List<String> jsonsList, double[] scoresList) {
		double max;
		double prevMax = 1000000;
		String[] topnjsons = new String[n];
		int i,j,ind;
		for (i = 0; i < n; ++i) {
			max = -999999; ind = 0;
			for (j = 0; j < scoresList.length; ++j) {
				if (max < scoresList[j] && scoresList[j] < prevMax) {	// Don't get previous maxes
					max = scoresList[j]; ind = j;		// Index of largest score
				}
			}
			topnjsons[i] = jsonsList.get(ind);	// Get the corresponding json with the largest score
			prevMax = max;
		}
		return topnjsons;	
	}

	// Sorted from highest to lowest
	public static double[] sort_score(double[] arr) {
		double max,tmp;
		int i,j,ind;
		for (i = 0; i < arr.length; ++i) {
			max = arr[i]; ind = i;
			for (j = i + 1; j < arr.length; ++j) {
				if (max < arr[j]) {
					max = arr[j]; ind = j;
				}
			}
			if (ind != i) {
				tmp = arr[i]; arr[i] = arr[ind]; arr[ind] = tmp;
			}
		}
		return arr;	
	}
	public static void main(String args[]) throws FileNotFoundException,IOException {
		int i,j,k,kk;
		boolean dontadd;
		String st, user_query, filename;
		List<String> allkeyvalues = new ArrayList<String>();	// Holds every single key-value pair

		// Load file
		filename = args[0];
		File f = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		while ((st = br.readLine()) != null) allkeyvalues.add(st);	// Assumes tweet jsons are on different lines

		// Get query from user
		System.out.println("Enter your query: ");
		Scanner user_input = new Scanner(System.in);
		user_query = user_input.nextLine();
		String[] query_terms = user_query.split("\\s");

		// Get Unique Query Terms and calculate qfi
		List<String> unique_query_terms = new ArrayList<String>();
		for (i = 0; i < query_terms.length; ++i) {
			dontadd = false;
			for (j = 0; j < unique_query_terms.size(); ++j) {
				if (query_terms[i].equals(unique_query_terms.get(j))) {
					dontadd = true; j = unique_query_terms.size();
				}
			}
			if (dontadd == false) unique_query_terms.add(query_terms[i]);
		}
		int ni,N;
		ni = N = 0;
		int[] n_i = new int[unique_query_terms.size()];
		int[] qfi_all = new int[unique_query_terms.size()];
		for (i = 0; i < unique_query_terms.size(); ++i) {
			qfi_all[i] = 0;
			for (j = 0; j < query_terms.length; ++j) {
				if (unique_query_terms.get(i).equals(query_terms[j])) ++qfi_all[i];
			}
		}

		// group1 is TERM, group2 is ALLTWEETJSONS
		String tjson = "(.*)   \"total count\" : (\\d+)(.*)";
		// group1 is NAME, group2 is SCREEN_NAME, group3 is LOCATION, group4 is CONTENT, group5 is PROFILE_IMG_URL, group6 is fi
		String pattern = ", \\[\"name\" : ([^\\]]*), \"screen name\" : ([^\\]]*), \"location\" : ([^\\]]*), \"content\" : \"([^\\]]*)\", \"profile image url\" : ([^\\]]*), \"frequency in tweet\" : (\\d+)\\]";
		Pattern tj = Pattern.compile(tjson);
		Matcher tjm;
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		
		for (i = 0; i < allkeyvalues.size(); ++i) {
			tjm = tj.matcher(allkeyvalues.get(i));
			while(tjm.find()) N += Integer.parseInt(tjm.group(2));
		}


		List<String> entirejsons = new ArrayList<String>();	// List to hold all individual jsons
		List<String> allContents = new ArrayList<String>();	// Holds all the tweet text of the above jsons
		List<String> allHandles = new ArrayList<String>();

		String jason,content,term,tweetjsons,screen_name;
		content = jason = term = screen_name = tweetjsons = "abc";

		// Calculate n_i values and build the list of tweets/documents
		for (k = 0; k < unique_query_terms.size(); ++k) {
			n_i[k] = 0;	// Initialize the ni for the this term as 0
			for (i = 0; i < allkeyvalues.size(); ++i) {
				tjm = tj.matcher(allkeyvalues.get(i));
				while(tjm.find()) {
					term = tjm.group(1); ni = Integer.parseInt(tjm.group(2)); tweetjsons = tjm.group(3);
				}
				// if this term exists in any of the tweets. Should only happen once per query term
				// if it doesn't exist, n_i[k] = 0
				if (unique_query_terms.get(k).equals(term)) {
					n_i[k] = ni;
					m = p.matcher(tweetjsons);			
					while(m.find()) {
						content = m.group(4); screen_name = m.group(2); jason = m.group(0).substring(2);
						if (allContents.size() == 0) {
							allContents.add(content); entirejsons.add(jason); allHandles.add(screen_name);
						}
						else {
							dontadd = false;
							for (kk = 0; kk < allContents.size();++kk) {
								// If this tweet exists in our jsons already, don't add it
								if (content.equals(allContents.get(kk)) && screen_name.equals(allHandles.get(kk))) {
									dontadd = true;
									kk = allContents.size();
								}	
							}
							if (dontadd == false) {
								allContents.add(content); entirejsons.add(jason); allHandles.add(screen_name);
							}
						}
					}
					i = allkeyvalues.size();	// Stop the loop early. Term should only match a single key
				}
			}
		}
		// Calculate score for each tweet
		double avg_doc_length = avgdl(allContents);
		double[] scoresList = new double[entirejsons.size()];	
		for (i = 0; i < entirejsons.size(); ++i) {
			scoresList[i] = BM25(unique_query_terms, allContents.get(i), n_i, qfi_all, N, avg_doc_length);
		}
		
		// Find top n Tweets and print them along with their scores
		int n = 2;	// TODO Update this to 100 later
		String[] topnjsons = toptweets(n,entirejsons,scoresList);	// Store top n tweet jsons in topnjsons
		scoresList = sort_score(scoresList);				// Sort the scores
		System.out.println("Relevant tweets");
		for (i = 0; i < topnjsons.length; ++i) 
			System.out.print((i+1) + ") " + topnjsons[i] + "\n\tScore: " + scoresList[i] + "\n");

		// Search Function
		System.out.println("Enter text to search for: ");
		Scanner new_scan = new Scanner(System.in);
		String second_query = new_scan.nextLine();
		int query_index = search(second_query, topnjsons);
		if (query_index == -1) System.out.println("Didn't find new query in the top n tweet");
		else System.out.println("Tweet is at rank " + query_index);
	}
}
