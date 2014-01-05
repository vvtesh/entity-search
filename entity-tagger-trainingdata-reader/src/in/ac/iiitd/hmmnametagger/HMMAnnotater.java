package in.ac.iiitd.hmmnametagger;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.toolbox.*;
import be.ac.ulg.montefiore.run.jahmm.learn.*;
import be.ac.ulg.montefiore.run.jahmm.draw.*;

/**
 * Understands transition probabilities from given tagged data.
 * The data has to be only integers (jahmm limitation). 
 * @author venkatesh
 *
 */
public class HMMAnnotater {

    static public void main(String[] argv) 
	throws java.io.IOException {

	  FileInputStream fstream = new FileInputStream("/media/venkatesh/Data/work/Entity Name Project/hmm/hmminput.txt");
	  DataInputStream in = new DataInputStream(fstream);
	  BufferedReader br = new BufferedReader(new InputStreamReader(in));
	  String strLine;
	
	  Vector sequences = new Vector();  
	  
	  while ((strLine = br.readLine()) != null)   {
		  System.out.println(strLine);
		  String[] words = strLine.split(",");
		  Vector sequence = new Vector();
		  for(String word: words) {
			  int value = Integer.parseInt(word);
			  ObservationInteger obs = new ObservationInteger(value);
			  sequence.add(obs);
		  }
		  sequences.add(sequence);
	  }
	

	/* K-Means approximation derives the initial HMM */
	KMeansLearner kml = new KMeansLearner(6, new OpdfIntegerFactory(6), sequences); 
	Hmm initHmm = kml.learn();
	(new GenericHmmDrawerDot()).write(initHmm, "/media/venkatesh/Data/work/Entity Name Project/hmm/initialHmm.dot");

	/* Baum-Welch learning refines the HMM for another 11 iterations*/
	BaumWelchLearner bwl =
	    new BaumWelchLearner();
	bwl.setNbIterations(3);	

	Hmm learntHmm = bwl.learn(initHmm, sequences);

	 
	(new GenericHmmDrawerDot()).write(learntHmm, "/media/venkatesh/Data/work/Entity Name Project/hmm/learntHmm.dot");
	
    }

}