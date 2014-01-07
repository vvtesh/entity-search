package in.ac.iiitd.tag;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;

/**
 * Modified version of HMM code found at http://sujitpal.blogspot.in/2008/11/ir-math-in-java-hmm-based-pos.html.
 * @author venkatesh
 *
 */
public class HMMBuilder {
	
	private static final String TEST = "/home/venkatesh/data/work/Entity Name Project/tst/sample.txt";
	private static final String TEST_WITH_TAGS = "/home/venkatesh/data/work/Entity Name Project/tst/sample with tags.txt";
	private static final String HMM_TAGGER = "/home/venkatesh/data/work/Entity Name Project/hmm/hmm_tagger.dat";
	private static final String HMM_DOTTAGGER = "/home/venkatesh/data/work/Entity Name Project/hmm/hmm_dottagger.dat";
	
	private static final String DATADICT = "/home/venkatesh/data/work/Entity Name Project/datadic.txt";
	public static final String DATADIR = "/home/venkatesh/data/work/Entity Name Project/datadir1";

	public static void main(String[] args) {
		 	
		    try {
		    	testHMMCreation();
		    	System.out.println("************************************");
				List<String> tagged = testPrediction();
				System.out.println("************************************");
				//dumpPRFScores(tagged);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private static String clean(String str) {
		  String cleanString = "";
		  str = str.toLowerCase().trim();
		  
		  String[] testWords = str.split(" ");
	      
	      for (String testWord: testWords) {
	    	  testWord = testWord.trim();
	    	  if (testWord.length() == 0) continue;
	    	  if (testWord.equalsIgnoreCase("&")) testWord = "and";
	    	  cleanString = cleanString + testWord + " ";
	      }
	      
	      return cleanString;
	}

	private static void dumpPRFScores(List<String> tagged) throws IOException {
		FileInputStream fstream = new FileInputStream(TEST_WITH_TAGS);
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;

		  String testSentence = "";
		  int lineNo = 0;
		  int correct = 0;
		  while ((testSentence = br.readLine()) != null)   {
		      testSentence = clean(testSentence.toLowerCase().trim());
		      String testSentenceWithTag = clean(tagged.get(lineNo).toLowerCase().trim());
		      
		      if (testSentence.equalsIgnoreCase(testSentenceWithTag)) {
		    	  correct++;
		    	  //System.out.println("RT:" + testSentence + ":" + testSentenceWithTag);
		      } else {
		    	  System.out.println("WG:" + testSentence + ":" + testSentenceWithTag);
		      }
		      
		      lineNo++;
		  }
		  double p = correct*1.0/lineNo;
		  
		  System.out.println(MessageFormat.format("Precision = {0}. Correct = {1}", String.format("%.2f",p), correct));
		  br.close();
		  in.close();
		  fstream.close();
		
	}

	private static void testHMMCreation() throws Exception {
		HmmTagger hmmTagger = new HmmTagger();
		hmmTagger.setDataDir(DATADIR);
		hmmTagger.setDictionaryLocation(DATADICT);
		hmmTagger.setHmmFileName(HMM_TAGGER);
		hmmTagger.setHmmDotFileName(HMM_DOTTAGGER);
		Hmm<ObservationInteger> hmm;
		
		hmm = hmmTagger.buildFromTrainingData();
		
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				if (Double.isNaN(hmm.getAij(i, j))) hmm.setAij(i, j, 0);
			}
		}
		
		hmmTagger.saveToFile(hmm);
		hmmTagger.saveToDotFile(hmm);
	}
	   
	  public static List<String> testPrediction() throws Exception {
		  
		List<String> taggedSentences = new ArrayList<String>();
		  
	    HmmTagger hmmTagger = new HmmTagger();
	    hmmTagger.setDataDir(DATADIR);
		hmmTagger.setDictionaryLocation(DATADICT);
		hmmTagger.setHmmFileName(HMM_TAGGER);
	    Hmm<ObservationInteger> hmm = hmmTagger.buildFromHmmFile();
	    
	      FileInputStream fstream = new FileInputStream(TEST);
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;

		  List<String> sequences = new ArrayList<String>();
		  String testSentence = "";
		  while ((testSentence = br.readLine()) != null)   {
			  String tagged = "";
		      testSentence = testSentence.toLowerCase().trim();
		      
		      String[] testWords = testSentence.split(" ");
		      
		      for (String testWord: testWords) {
		    	  testWord = testWord.toLowerCase().trim();
		    	  if (testWord.length() == 0) continue;
			      
			      NameParts wordPos = NameParts.cn;
			      try {
				      wordPos = hmmTagger.getMostLikelyPos(testWord, 
				        testSentence, hmm); 
				      tagged = tagged + testWord + "\\" + wordPos + " ";
			      } catch (NullPointerException npe) {	    
			    	  npe.printStackTrace();
			    	  
			      }
			      catch (Exception e) {
			    	  e.printStackTrace();	    	  
			      }		  
			      
		      }
		      System.out.println(tagged);
		      taggedSentences.add(tagged);
		  }
		  br.close();
		  in.close();
		  fstream.close();
		  return taggedSentences;
	    
	  }
}
