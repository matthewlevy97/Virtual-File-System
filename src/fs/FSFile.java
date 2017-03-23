package fs;

import java.util.Arrays;

public class FSFile {
	
	private String fileName;  // Name of file
	private byte[] contents;  // Data inside file
	private int position;     // Position to read/write from/to
	
	/**
	 * Creates an empty file with a given file name
	 * 
	 * @param fileName
	 */
	public FSFile(String fileName) {
		this(fileName, new byte[FileSystem.FILE_BLOCK_SIZE]);
	}
	/**
	 * Load a file with given file name and data
	 * 
	 * @param fileName
	 * @param contents
	 */
	public FSFile(String fileName, byte[] contents) {
		this.fileName = fileName;
		this.contents = contents;
		
		this.position = 0;
		
		if(this.contents.length != FileSystem.FILE_BLOCK_SIZE) {
			System.err.println(String.format("FSFile content length is incorrect. Should be %i. Length is %i", FileSystem.FILE_BLOCK_SIZE, this.contents.length));
		}
	}
	
	/**
	 * Returns the file name
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Returns the entire contents of the file
	 * 
	 * @return byte[] - contents of file
	 */
	public byte[] read() {
		return contents;
	}
	
	/**
	 * Returns the contents of the file from the current position to position + length
	 * 
	 * @param length
	 * @return byte[] - contents of file
	 */
	public byte[] read(int length) {
		return Arrays.copyOfRange(contents, position, Math.min(position + length, FileSystem.FILE_BLOCK_SIZE - position));
	}
	
	/**
	 * Returns the contents of the file from the given offset to offset + length
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public byte[] read(int offset, int length) {
		return Arrays.copyOfRange(contents, offset, Math.min(offset + length, FileSystem.FILE_BLOCK_SIZE - offset));
	}
	
	/**
	 * Sets the seek position on the file to 'position'
	 * If 0 < param < contents.length, then set position to param
	 * Else, set position to 0
	 * 
	 * @param position
	 */
	public void seek(int position) {
		this.position = (position < contents.length && position >= 0) ? position : 0;
	}
	
	/**
	 * Write data to the file starting at current position
	 * All data written to file overwrites current data
	 * 
	 * @param data
	 */
	public void write(byte[] data) {
		for(int i = position; i < data.length; i++) {
			contents[i] = data[i - position];
		}
	}
	
	/**
	 * toString() method
	 */
	public String toString() {
		return String.format("<File Name: %s>", fileName);
	}
}
