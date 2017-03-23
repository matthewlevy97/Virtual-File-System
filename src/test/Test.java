package test;

import fs.FSFile;
import fs.FileSystem;

public class Test {
	
	public static void main(String[] args) {
		FileSystem fs = new FileSystem();
		
		//fs.clean();
		
		FSFile file = fs.open("file1");
		file.write("This is a test".getBytes());
		fs.save(file);
		
		file = fs.open("file2");
		file.write("HELLO WORLD".getBytes());
		fs.save(file);
		
		file = fs.open("file1");
		System.out.println(new String(file.read()));
		
		file = fs.open("file2");
		System.out.println(new String(file.read()));
		
		file = fs.open("file1");
		System.out.println(new String(file.read()));
		
		fs.close();
		
	}
	
}
