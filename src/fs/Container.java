package fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Container {

	private RandomAccessFile container;

	/**
	 * Holds the contents of the file system
	 */
	public Container() {
		File f = new File(FileSystem.FILE_SYSTEM_CONTAINER);

		if(!init(f)) {
			// Create File and retry
			try {
				f.createNewFile();
				init(f);
			} catch (IOException e) {}
		}

		if(container == null) {
			System.err.println("Could not open file system container");
		}

	}

	/**
	 * Try to open the container file
	 * 
	 * @param f
	 * @return
	 */
	private boolean init(File f) {
		try {
			container = new RandomAccessFile(f, "rw");
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * Completely deletes the file system
	 * This is unrecoverable
	 * 
	 * Has no modifier to prevent being called outside of current package
	 * 
	 * @return
	 */
	boolean clean() {
		// Clean container by truncating data to 0 bytes
		try {
			container.setLength(0);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Returns true if the container file is open currently
	 * 
	 * @return
	 */
	public boolean isOpen() {
		return (container == null) ? false : true;
	}

	/**
	 * Returns the size of the file system container, or 0 if an error occurs
	 * 
	 * @return
	 */
	public long getContainerSize() {
		try {
			return container.length();
		} catch (IOException e) {
			System.err.println("Error getting file system container length");
		}
		return 0;
	}

	/**
	 * Returns the number of files stored in the file container
	 * 
	 * @return
	 */
	public long getNumberOfFiles() {
		return getContainerSize() / FileSystem.FILE_BLOCK_SIZE;
	}

	/**
	 * Gets a file from the file system container starting at a given offset
	 * 
	 * @param l
	 * @return
	 */
	public byte[] getFile(long l) {
		// Do not do anything if container file was not open, offset is not at the start of a file block, or offset > container size
		if(!isOpen() || l < 0 || l % FileSystem.FILE_BLOCK_SIZE != 0 || l > getContainerSize()) {
			return null;
		}

		// Create buffer to read into
		byte[] data = new byte[FileSystem.FILE_BLOCK_SIZE];
		try {
			// Move to offset
			container.seek(l);

			// Read file block into buffer
			container.read(data);
		} catch (IOException e) {
			System.err.println("Error reading from file system container");
		}

		return data;
	}

	/**
	 * Saves a file to the container
	 * 
	 * @param metadata
	 * @param file
	 */
	public void saveFile(Metadata metadata, FSFile file) {
		if(!isOpen()) {
			return;
		}

		if(!metadata.getFileName().equals(file.getFileName())) {
			// Metadata doesn't match file, error
			System.err.println("Metadata does not match file data. Different file names.");
			return;
		}

		// Get offset
		long offset = metadata.getOffset();

		try {
			container.seek(offset);
			container.write(file.read());
		} catch (IOException e) {
			System.err.println(String.format("Error writing %s to file system container", metadata.getFileName()));
		}
	}

	/**
	 * Adds a file to the file system container
	 * Returns the offset of the added file
	 * 
	 * @param f
	 * @return
	 */
	public long addFile(FSFile file) {
		if(!isOpen()) {
			return -1;
		}

		try {
			long offset = getContainerSize();

			// Move to end of file
			container.seek(offset);
			// Append Data
			container.write(file.read());
			return offset;
		} catch (IOException e) {
			System.err.println("Error adding file to file system container");
		}
		return -1;
	}

	/**
	 * Closes the file system container
	 */
	public void close() {
		if(!isOpen()) {
			return;
		}

		try {
			container.close();
		} catch (IOException e) {
			System.err.println("Error closing file system container");
		}

		// Clear the reference to the file system container
		container = null;
	}
}
