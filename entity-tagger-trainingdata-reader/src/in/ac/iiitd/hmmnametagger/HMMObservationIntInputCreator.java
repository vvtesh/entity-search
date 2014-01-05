package in.ac.iiitd.hmmnametagger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from HmmExample_coin.
 * Reads the annotated dataset and creates observations to int.
 * For eg.
 *  abc\cn shop\ca 
 * will become
 *  1,2 
 *  where 1 = cn and 2 = ca.
 * @author venkatesh
 *
 */
public class HMMObservationIntInputCreator {
	
	public static void main(String[] args) {
		try {
			(new HMMObservationIntInputCreator()).createHMMInputData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createHMMInputData() throws IOException {
		  
		  //Read file, tokenize, construct observations.
		  FileInputStream fstream = new FileInputStream("/media/venkatesh/Data/work/Entity Name Project/datadir/dataset.txt");
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;

		  List<String> sequences = new ArrayList<String>();
		  while ((strLine = br.readLine()) != null)   {
			  List<String> sequence = new ArrayList<String>();
			  String[] words = strLine.split(" ");
			  String output = "";
			  for(int i=0; i<words.length; i++) {
				  if (words[i].length() > 1) {
					  String[] annotation = words[i].split("\\\\");
					  if (annotation.length < 2) continue;
					  int annotationInt = 0;
					  switch(annotation[annotation.length-1].toLowerCase()){
					  	case "cn": annotationInt = 1; break;	
					  	case "ca": annotationInt = 2; break;
					  	case "lo": annotationInt = 3; break;
					  	case "an": annotationInt = 3; break;
					  	case "ot": annotationInt = 4; break;
					  }
					  if (annotationInt > 0) {
						  output = output + annotationInt;	
						  if (i < (words.length -1)) output = output + ","; 
						  
					  }
					  
				  }
				  
			  }
			  output = output + ",5";
			  sequences.add(output);
		  }
		  
		  StringBuilder sb = new StringBuilder();
		  for(String sequence:sequences) {
			  sb.append(sequence);
			  sb.append("\n");
		  }
		  
		  FileWriter writer = new FileWriter("/media/venkatesh/Data/work/Entity Name Project/hmm/hmminput.txt");
		  writer.write(sb.toString());
		  writer.close();
	}
}
