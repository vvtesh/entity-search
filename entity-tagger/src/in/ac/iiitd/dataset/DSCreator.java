package in.ac.iiitd.dataset;

import in.ac.iiitd.tag.HMMBuilder;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Creates a synthetic dataset of business entity names.
 * Uses list of anchors (Pvt, ltd, co, corp,...), categories (school, pharmacy, institute, ...) and names (wiley, crossword, ...).
 * Since we use the known components, tagging becomes easy. 
 * @output A dataset with entity names with every word tagged.
 * @author venkatesh
 *
 */
public class DSCreator {
	
	private static final String ANCHOR = "/home/venkatesh/data/work/Entity Name Project/manual-ds/anchors";
	private static final String CATEGORY = "/home/venkatesh/data/work/Entity Name Project/manual-ds/Category Words";
	private static final String NAME = "/home/venkatesh/data/work/Entity Name Project/manual-ds/name words";

	private static final String DATASET_FILE = HMMBuilder.DATADIR;

	private static List<String> anchors = null;
	private static List<String> categories = null;
	private static List<String> names = null;
	
	private static List<String> dataset = new ArrayList<String>();
	
	public static void main(String[] args) {
		try {
			loadFiles(); //Take all inputs
			normalize(); //Clean the inputs 
			createDataSet(); //make full entity names using the inputs and store them with annotations.
			System.out.println("DataSet Created.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void createDataSet() {
		String businessName = "";
		dataset.clear();
		
		Random randomGenerator = new Random();
		int namesCount = names.size();
		int anchorsCount = anchors.size();
		for(String category: categories) {
			String name = names.get(randomGenerator.nextInt(namesCount));
			String anchor = anchors.get(randomGenerator.nextInt(anchorsCount));
			name = annotate(name,"CN");
			anchor = annotate(anchor, "AN");
			category = annotate(category, "CA");
			businessName = MessageFormat.format("{0} {1} {2}", name, category, anchor);
			dataset.add(businessName);
		}
		
		for(String category: categories) {
			String name = names.get(randomGenerator.nextInt(namesCount));
			String anchor = anchors.get(randomGenerator.nextInt(anchorsCount));
			name = annotate(name,"CN");
			category = annotate(category, "CA");
			businessName = MessageFormat.format("{0} {1}", name, category);
			dataset.add(businessName);
		}
		
		for(String category: categories) {
			String name = names.get(randomGenerator.nextInt(namesCount));
			String anchor = anchors.get(randomGenerator.nextInt(anchorsCount));
			name = annotate(name,"CN");
			anchor = annotate(anchor, "AN");
			businessName = MessageFormat.format("{0} {1}", name, anchor);
			dataset.add(businessName);
		}
		
		for(String anchor: anchors) {
			String name = names.get(randomGenerator.nextInt(namesCount));
			name = annotate(name,"CN");
			anchor = annotate(anchor, "AN");
			businessName = MessageFormat.format("{0} {1}", name, anchor);
			dataset.add(businessName);
		}
		
		for(String name:  names) {
			String category = categories.get(randomGenerator.nextInt(categories.size()));
			category = annotate(category,"CA");
			name = annotate(name, "CN");
			businessName = MessageFormat.format("{0} {1}", name, category);
			dataset.add(businessName);
		}		
		
		saveDataSet(DATASET_FILE);
	}
	
	

	private static void saveDataSet(String datasetFile) {
		String content = "";
		for(String name: dataset) {
			content = content + name + "\n";
		}
		
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(datasetFile), "utf-8"));
		    writer.write(content);
		} catch (IOException ex){
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
		
	}

	private static String annotate(String item, String annotation) {
		String newString = "";
		String[] tokens = item.split(" ");
		for(String token: tokens) {
			if (token.equalsIgnoreCase("and")) {
				newString = newString + token + "\\OT ";
			} else {
				newString = newString + token + "\\" + annotation + " ";
			}
		}
		newString.trim();
		return newString;
	}

	private static void normalize() {
		// TODO Auto-generated method stub
		
	}

	private static void loadFiles() throws IOException {
		anchors = FileUtil.loadStrings(ANCHOR);
		categories = FileUtil.loadStrings(CATEGORY);
		names = FileUtil.loadStrings(NAME);
				
	}
}
