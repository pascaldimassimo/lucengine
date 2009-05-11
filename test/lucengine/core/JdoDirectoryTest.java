package lucengine.core;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.Test;

public class JdoDirectoryTest extends JdoTest {

    @Test
    public void delete_works() {
	IndexFile file = createFile();
	JdoDirectory dir = new JdoDirectory(dao);
	try {
	    dir.deleteFile(file.getFilename());
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}

	Assert.assertNull(dao.getByFilename(file.getFilename()));
    }

    @Test
    public void file_exists_works() {
	IndexFile file = createFile();
	JdoDirectory dir = new JdoDirectory(dao);
	try {
	    String filename = file.getFilename();
	    Assert.assertTrue(dir.fileExists(filename));
	    dir.deleteFile(filename);
	    Assert.assertFalse(dir.fileExists(filename));
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void file_length_works() {
	IndexFile file = createFile();
	JdoDirectory dir = new JdoDirectory(dao);
	try {
	    long len = dir.fileLength(file.getFilename());
	    Assert.assertEquals(file.getData().getBytes().length, len);
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void list_works() {
	JdoDirectory dir = new JdoDirectory(dao);
	try {
	    Assert.assertEquals(0, dir.list().length);
	    IndexFile file = createFile();

	    String[] filenames = dir.list();
	    Assert.assertEquals(1, filenames.length);
	    Assert.assertEquals(file.getFilename(), filenames[0]);

	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void rename_works() {
	JdoDirectory dir = new JdoDirectory(dao);
	IndexFile file = createFile();
	String from = file.getFilename();
	String to = from + "_rename";
	try {
	    Assert.assertTrue(dir.fileExists(from));
	    dir.renameFile(from, to);
	    Assert.assertFalse(dir.fileExists(from));
	    Assert.assertTrue(dir.fileExists(to));
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void throw_exception_if_not_existing_on_open() throws IOException {
	JdoDirectory dir = new JdoDirectory(dao);
	String filename = "toto.txt";
	Assert.assertFalse(dir.fileExists(filename));

	try {
	    dir.openInput(filename);
	    Assert.fail("Should have throw FileNotFoundException");
	} catch (FileNotFoundException e) {
	    // expected
	}
    }

    @Test
    public void get_output_create_file_if_not_existing() {
	JdoDirectory dir = new JdoDirectory(dao);
	String filename = "toto.txt";
	try {
	    Assert.assertFalse(dir.fileExists(filename));
	    dir.createOutput(filename);
	    Assert.assertTrue(dir.fileExists(filename));
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void write_read() {
	JdoDirectory dir = new JdoDirectory(dao);
	String filename = "toto.txt";
	try {
	    String str = "allo toi!!!";
	    int i = 99;

	    IndexOutput out = dir.createOutput(filename);
	    out.writeString(str);
	    out.writeInt(i);
	    out.close();

	    // Read back
	    IndexInput in = dir.openInput(filename);
	    Assert.assertEquals(str, in.readString());
	    Assert.assertEquals(i, in.readInt());

	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }
    
    @Test
    public void write_read_with_flush() {
	JdoDirectory dir = new JdoDirectory(dao);
	String filename = "toto.txt";
	try {
	    String str = "allo toi!!!";
	    int i = 99;

	    IndexOutput out = dir.createOutput(filename);
	    out.writeString(str);
	    out.flush();
	    out.writeInt(i);
	    out.close();

	    // Read back
	    IndexInput in = dir.openInput(filename);
	    Assert.assertEquals(str, in.readString());
	    Assert.assertEquals(i, in.readInt());

	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }

    @Test
    public void seek_works() throws IOException {
	JdoDirectory dir = new JdoDirectory(dao);
	String filename = "toto.txt";
	int i = 1;
	int j = 2;
	int k = 3;

	IndexOutput out = dir.createOutput(filename);
	long pos = out.getFilePointer();
	out.writeInt(i);
	out.writeInt(j);
	// Go back at the beginning to write another int
	out.seek(pos);
	out.writeInt(k);
	out.close();

	// Read back
	IndexInput in = dir.openInput(filename);
	// int i should have been overwritten
	Assert.assertEquals(k, in.readInt());
	// j should still be there
	Assert.assertEquals(j, in.readInt());
    }

    //@Test
    public void test_ram() throws IOException {
	// TODO remove this test!

	RAMDirectory dir = new RAMDirectory();
	String filename = "toto.txt";
	int i = 99;
	int j = 98;
	IndexOutput out = dir.createOutput(filename);
	out.writeInt(i);
	out.flush();
	out.writeInt(j);
	out.close();
	
	IndexInput in = dir.openInput(filename);
	Assert.assertEquals(i, in.readInt());
	Assert.assertEquals(j, in.readInt());

    }

}
