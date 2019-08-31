package NewTime.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class StringScan {
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		StringScan scanner = null;
		
		System.out.print("File/Directory to search within: ");
		String path = in.nextLine();
		scanner = scanner.load(path);
			
		System.out.print("String to search for: ");
		String query = in.nextLine();
			
		System.out.print("Searching..");
		String[] output = scanner.search(query);
		System.out.println(" results:");
			
		for(int i = 0; i < output.length; i++) {
			System.out.println(output[i]);
		}
	}
	
	private File[] files;
		
	private StringScan(File file) {
		if(file.isDirectory()) {
			loadDirectory(file);
		}else {
			files = new File[1];
			files[0] = file;
		}
	}
	
	private int loadDirectory(File dir) {
		int total = 0;
		File[] list = dir.listFiles();
		for(int i = 0; i < list.length; i++) {
			if(list[i].isDirectory()) {
				total += loadDirectory(list[i]);
			}else {
				if(loadFile(list[i])) {
					total++;
				}
			}
		}
		return total;
	}
	
	private boolean loadFile(File file) {
		if(this.files == null) {
			this.files = new File[32];
		}
		int i = this.appendArray(files, file);
		if(i > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Create a new instance of StringScan with the given files/directories to scan.
	 * 
	 * @param path The path where the file/directory is located.
	 * @return StringScan an instance with all files found loaded into memory.
	 */
	public static StringScan load(String path) {
		File file = new File(path);
		if(file.exists()) {
			return new StringScan(file);
		}
		System.err.println("No such file/directory: " + file.getAbsolutePath());
		return null;
	}

	/**
	 * 
	 * @param query The string to search for.
	 * @return String[] An array containing the absolute locations of the files containing the string and the line numbers the string is located at.
	 */
	public String[] search(String query) {
		String[] found = new String[this.files.length];
		for(int i = 0; i < this.files.length; i++) {
			if(files[i] != null) {
				if(isValidFile(files[i])) {
					Integer[] lines = findQuery(files[i], query);
					if(lines != null) {
						buildSearchString(found, i, lines);
					}
				}
			}
		}		
		found = clean(found);
		return found;
	}
	
	private void buildSearchString(String[] found, int index, Integer[] lines) {
		boolean first = true;
		found[index] = this.files[index].getAbsolutePath() + ":";
		for(int i = 0; i < lines.length; i++) {
			if(lines[i] != null) {
				if(first) {
					found[index] = found[index] + lines[i].intValue();
					first = false;
				}else {
					found[index] = found[index] + "," + lines[i].intValue();
				}
			}
		}
	}
	
	private Integer[] findQuery(File file, String query) {
		int current = 0;
		int found = 0;
		Integer[] buffer = new Integer[64];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.contains(query)) {
					appendArray(buffer, current);
					found++;
				}
				current++;
			}
			reader.close();
			reader = null;
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		if(found == 0) {
			buffer = null;
		}
		return buffer;
	}
	
	private boolean isValidFile(File file) {
		if(file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
	
	private int appendArray(Object[] array, Object value) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] == null) {
				array[i] = value;
				return i;
			}
		}
		array = Arrays.copyOf(array, array.length+1);
		array[array.length-1] = value;
		return array.length-1;
	}
	
	private String[] clean(String[] array) {
		int total = 0;
		for(int i = 0; i < array.length; i++) {
			if(array[i] != null) {
				total++;
			}
		}
		
		String[] buffer = new String[total];
		for(int i = 0; i < buffer.length; i++) {
			for(int c = 0; c < array.length; c++) {
				if(array[c] != null) {
					buffer[i] = array[c];
					array[c] = null;
					break;
				}
			}
		}
		
		return buffer;
	}
	
}
