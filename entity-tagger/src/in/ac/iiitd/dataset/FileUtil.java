package in.ac.iiitd.dataset;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple file utility to load/store files.
 * @author venkatesh
 *
 */
public class FileUtil {
	
	/**
	 * Drops blank lines.
	 * Trims & converts to lower case.
	 * Reduces intermediate spaces to 1.
	 * Removes , -, _, (, ) and slashes
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static List<String> loadStrings(String fileName) throws IOException {
		List<String> strings = new ArrayList<String>();
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;

		while ((strLine = br.readLine()) != null)   {	
			strLine = strLine.trim().toLowerCase();
			if (strLine.length()== 0) continue;
			String[] words = strLine.split(" ");
			String newLine = "";
			for(String word: words) {
				word = word.trim();				
				newLine = newLine + word + " ";				
			}
			newLine = newLine.replace(",", "");
			newLine = newLine.replace("(", "");
			newLine = newLine.replace(")", "");
			newLine = newLine.replace("-", "");
			newLine = newLine.replace("_", "");
			newLine = newLine.replace("/", "");
			newLine = newLine.replace("\\\\", "");
			strings.add(newLine);
		}
		return strings;
	}
}
