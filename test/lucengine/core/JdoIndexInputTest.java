package lucengine.core;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class JdoIndexInputTest extends JdoTest {

    @Test
    public void read_bytes_works() {
	IndexFile file = createFile();
	byte[] bytes = file.getData().getBytes();

	JdoIndexInput input = new JdoIndexInput(file, dao);
	try {
	    for (byte b : bytes) {
		Assert.assertEquals(b, input.readByte());
	    }
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	}
    }
}
