package lucengine.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.lucene.store.BufferedIndexInput;

public class JdoIndexInput extends BufferedIndexInput {

    IndexFile file;
    IndexFileDao dao;

    public JdoIndexInput(IndexFile file, IndexFileDao dao) {
	this.file = file;
	this.dao = dao;
    }

    @Override
    protected void readInternal(byte[] b, int offset, int len) throws IOException {	
	ByteArrayInputStream bin = new ByteArrayInputStream(file.getData().getBytes());
	bin.skip(getFilePointer());
	try {
	    bin.read(b, offset, len);
	} catch (IndexOutOfBoundsException e) {
	    throw new IOException(e);
	}
    }

    @Override
    protected void seekInternal(long pos) throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public long length() {
	return file.getData().getBytes().length;
    }
}
