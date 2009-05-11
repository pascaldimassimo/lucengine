package lucengine.core;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class JdoIndexOutputTest extends JdoTest {

    @Test
    public void write_byte_works() throws IOException {
	IndexFile file = createEmptyFile();

	byte b = (byte) 0xF;
	JdoIndexOutput output = new JdoIndexOutput(file, dao);
	output.writeByte(b);
	output.close();

	IndexFile file2 = dao.getByFilename(file.getFilename());
	byte[] bytes = file2.getData().getBytes();
	Assert.assertEquals(1, bytes.length);
	Assert.assertEquals(b, bytes[0]);
    }

    @Test
    public void write_byte_array_works() {
	IndexFile file = createFile();

	byte[] expected = "allo".getBytes();
	JdoIndexOutput output = new JdoIndexOutput(file, dao);
	try {
	    output.writeBytes(expected, 0, 4);
	    output.close();
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}

	IndexFile file2 = dao.getByFilename(file.getFilename());
	byte[] bytes = file2.getData().getBytes();
	// Check end of buffer (since we did a append)
	int from = bytes.length - expected.length;
	int to = from + expected.length;
	byte[] lasts = Arrays.copyOfRange(bytes, from, to);
	Assert.assertArrayEquals(expected, lasts);
    }

    @Test
    public void length_works() {
	IndexFile file = createEmptyFile();
	JdoIndexOutput output = new JdoIndexOutput(file, dao);
	try {
	    String str = "toto";
	    output.writeString(str);

	    // Empty before close
	    Assert.assertEquals(0, output.length());

	    // +1 EOS byte
	    output.close();
	    Assert.assertEquals(str.length() + 1, output.length());

	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }
}
