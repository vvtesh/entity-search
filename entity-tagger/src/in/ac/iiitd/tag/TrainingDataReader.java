package in.ac.iiitd.tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import Jama.Matrix;

/**
 * Reads a file or directory of tagged text from training Corpus and
 * computes the various probability matrices for the HMM.
 */
public class TrainingDataReader {

  private final Log log = LogFactory.getLog(getClass());
  
  private String dataFilesLocation;
  private String wordDictionaryLocation;
  private boolean debug;

  private Bag<String> piCounts = new HashBag<String>();
  private Bag<String> aCounts = new HashBag<String>();
  private Map<String,Double[]> wordPosMap = 
    new HashMap<String,Double[]>();
  
  private Matrix pi;
  private Matrix a;
  private Matrix b;
  private List<String> words;
  
  public void setDataFilesLocation(String dataFilesLocation) {
    this.dataFilesLocation = dataFilesLocation;
  }
  
  public void setWordDictionaryLocation(String wordDictionaryLocation) {
    this.wordDictionaryLocation = wordDictionaryLocation;
  }
  
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void read() throws Exception {
    File location = new File(dataFilesLocation);
    File[] inputs;
    if (location.isDirectory()) {
      inputs = location.listFiles();
    } else {
      inputs = new File[] {location};
    }
    int currfile = 0;
    int totfiles = inputs.length;
    for (File input : inputs) {
      if (input.getName().endsWith("~")) continue;
      currfile++;
      log.info("Processing file (" + currfile + "/" + totfiles + "): " + 
        input.getName());
      BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(input)));
      String line;
      while ((line = reader.readLine()) != null) {
        if (StringUtils.isEmpty(line)) {
          continue;
        }
        StringTokenizer tok = new StringTokenizer(line, " ");
        int wordIndex = 0;
        NameParts prevPos = null;
        while (tok.hasMoreTokens()) {
          String taggedWord = tok.nextToken();
          String[] wordTagPair = StringUtils.split(
            StringUtils.lowerCase(StringUtils.trim(taggedWord)), "\\\\");
          if (wordTagPair.length != 2) {
            continue;
          }
          NameParts pos = NameParts.valueOf(wordTagPair[1]);
          if (! wordPosMap.containsKey(wordTagPair[0])) {
            // create an entry
            Double[] posProbs = new Double[NameParts.values().length];
            for (int i = 0; i < posProbs.length; i++) {
              posProbs[i] = new Double(0.0D);
            }
            wordPosMap.put(wordTagPair[0], posProbs);
          }
          Double[] posProbs = wordPosMap.get(wordTagPair[0]);
          posProbs[pos.ordinal()] += 1.0D;
          wordPosMap.put(wordTagPair[0], posProbs);
          if (wordIndex == 0) {
            // first word, update piCounts
            piCounts.add(pos.name());
          } else {
            aCounts.add(StringUtils.join(new String[] {
              prevPos.name(), pos.name()}, ":"));
          }
          prevPos = pos;
          wordIndex++;
        }
      }
      reader.close();
    }
    // normalize counts to probabilities
    int numPos = NameParts.values().length;
    // compute pi
    pi = new Matrix(numPos, 1);
    for (int i = 0; i < numPos; i++) {
      pi.set(i, 0, piCounts.getCount((NameParts.values()[i]).name()));
    }
    pi = pi.times(1 / pi.norm1());
    // compute a
    a = new Matrix(numPos, numPos);
    for (int i = 0; i < numPos; i++) {
      for (int j = 0; j < numPos; j++) {
        a.set(i, j, aCounts.getCount(StringUtils.join(new String[] {
          (NameParts.values()[i]).name(), (NameParts.values()[j]).name()
        }, ":")));
      }
    }
    // compute b
    int numWords = wordPosMap.size();
    words = new ArrayList<String>();
    words.addAll(wordPosMap.keySet());
    b = new Matrix(numPos, numWords);
    for (int i = 0; i < numPos; i++) {
      for (int j = 0; j < numWords; j++) {
        String word = words.get(j);
        b.set(i, j, wordPosMap.get(word)[i]);
      }
    }
    // normalize across rows for a and b (sum of cols in each row == 1.0)
    for (int i = 0; i < numPos; i++) {
      double rowSumA = 0.0D;
      for (int j = 0; j < numPos; j++) {
        rowSumA += a.get(i, j);
      }
      for (int j = 0; j < numPos; j++) {
        a.set(i, j, (a.get(i, j) / rowSumA));
      }
      double rowSumB = 0.0D;
      for (int j = 0; j < numWords; j++) {
        rowSumB += b.get(i, j);
      }
      for (int j = 0; j < numWords; j++) {
        b.set(i, j, (b.get(i, j) / rowSumB));
      }
    }
    // write out brown word dictionary for later use
    writeDictionary();
    // debug
    if (debug) {
      pi.print(8, 4);
      a.print(8, 4);
      b.print(8, 4);
      System.out.println(words.toString());
    }
  }
  
  public List<String> getWords() {
    return words;
  }
  
  public double[] getPi() {
    double[] pia = new double[pi.getRowDimension()];
    for (int i = 0; i < pia.length; i++) {
      pia[i] = pi.get(i, 0);
    }
    return pia;
  }
  
  public double[][] getA() {
    double[][] aa = new double[a.getRowDimension()][a.getColumnDimension()];
    for (int i = 0; i < a.getRowDimension(); i++) {
      for (int j = 0; j < a.getColumnDimension(); j++) {
        aa[i][j] = a.get(i, j);
      }
    }
    return aa;
  }
  
  public double[][] getB() {
    double[][] ba = new double[b.getRowDimension()][b.getColumnDimension()];
    for (int i = 0; i < b.getRowDimension(); i++) {
      for (int j = 0; j < b.getColumnDimension(); j++) {
        ba[i][j] = b.get(i, j);
      }
    }
    return ba;
  }

  private void writeDictionary() throws Exception {
    FileWriter dictWriter = new FileWriter(wordDictionaryLocation);
    for (String word : words) {
      dictWriter.write(word + "\n");
    }
    dictWriter.flush();
    dictWriter.close();
  }
}