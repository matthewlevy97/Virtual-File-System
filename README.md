# What Is This?
A virtual file system created in Java that can be used to store multiple files inside a transportable container.

# Terms
* container - A single file holding all files/blocks
* metadata table - A json file that contains the filename, location, and offset for every file inside of the container
* table/metadata entry - A metadata entry in the metadata table that contains data on the length and offset of a file with a given file name
* file/block - A chunk of bytes stored in the container (Can be used interchangeably)
* length - The length of a file/block (Currently 512 bytes)
* offset - The number of bytes from the start of the container to the start of the file/block

# How Does It Work?
All files are allocated 512 bytes (1/2 KB) in the container file.
These files are then padded with null bytes to max size and stored in binary in the container.

When opening a file, the file system will:
  1) Scan through the metadata table looking for the file name
  2) If the file name was found:
     * The offset, obtained from the table, is used to read from the container
     * The data is saved and returned as a FSFile Object
  3) Else:
     * A new block will be appended to the end of the container
     * A new metadata entry is created containing the offset returned by the container, the files name, and the length of the file
     * The metadata entry is saved to the metadata table
     * An empty block is returned as a FSFile Object

# Current Limitations
* Max file size of 512 bytes (Can be changed)
* Does not allow data to span over multiple blocks
* Can grow in size quickly due to each block being 512 bytes no matter the content (Empty file still takes 512 bytes)
