package in.ac.iiitd.tag;

import java.text.MessageFormat;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;

/**
 * Modified version of HMM code found at http://sujitpal.blogspot.in/2008/11/ir-math-in-java-hmm-based-pos.html.
 * @author venkatesh
 *
 */
public class HMMBuilder {

	private static final String HMM_TAGGER = "/media/venkatesh/Data/work/Entity Name Project/hmm_tagger.dat";
	private static final String DATADICT = "/media/venkatesh/Data/work/Entity Name Project/datadic.txt";
	private static final String DATADIR = "/media/venkatesh/Data/work/Entity Name Project/datadir";

	public static void main(String[] args) {
		 	
		    try {
		    	testHMMCreation();
				testPrediction();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private static void testHMMCreation() throws Exception {
		HmmTagger hmmTagger = new HmmTagger();
		hmmTagger.setDataDir(DATADIR);
		hmmTagger.setDictionaryLocation(DATADICT);
		hmmTagger.setHmmFileName(HMM_TAGGER);
		Hmm<ObservationInteger> hmm;
		
		hmm = hmmTagger.buildFromTrainingData();
		hmmTagger.saveToFile(hmm);
		
	}
	   
	  public static void testPrediction() throws Exception {
	    HmmTagger hmmTagger = new HmmTagger();
	    hmmTagger.setDataDir(DATADIR);
		hmmTagger.setDictionaryLocation(DATADICT);
		hmmTagger.setHmmFileName(HMM_TAGGER);
	    Hmm<ObservationInteger> hmm = 
	      hmmTagger.buildFromHmmFile();
	    String[] testSentences = new String[] {
	    		
	    		 "Delhii public school"
	    };
	    
	    for (int i = 0; i < testSentences.length; i++) {
	      String tagged = "";
	      testSentences[i] = testSentences[i].toLowerCase();
	      
	      String[] testWords = testSentences[i].split(" ");
	      for (String testWord: testWords) {
		      if (!testSentences[i].toLowerCase().contains(testWord)) {
		    	  tagged = tagged + testWord + "\\NF" + " ";
		    	  continue;
		      }
		      NameParts wordPos = NameParts.cn;
		      try {
			      wordPos = hmmTagger.getMostLikelyPos(testWord, 
			        testSentences[i], hmm); 
			      tagged = tagged + testWord + "\\" + wordPos + " ";
		      } catch (NullPointerException npe) {	    
		    	  npe.printStackTrace();
		    	  
		    	  //System.out.print("Guessing... ");
		    	  //System.out.println("Pos(" + testWords[i] + ")=" + wordPos);
		      }
		      catch (Exception e) {
		    	  e.printStackTrace();	    	  
		      }		  
		      
	      }
	      System.out.println(tagged);
	    }
	  }
}
