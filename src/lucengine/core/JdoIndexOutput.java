package lucengine.core;

import java.io.IOException;

import org.apache.lucene.store.BufferedIndexOutput;

import com.google.appengine.api.datastore.Blob;

public class JdoIndexOutput extends BufferedIndexOutput {

    IndexFile file;
    IndexFileDao dao;

    public JdoIndexOutput(IndexFile file, IndexFileDao dao) {
	this.file = file;
	this.dao = dao;
    }

    @Override
    public long length() throws IOException {
	return file.getData().getBytes().length;
    }

    @Override
    protected void flushBuffer(byte[] b, int offset, int len) throws IOException {
	if (getFilePointer() > Integer.MAX_VALUE) {
	    throw new IOException("Position is not an integer");
	}

	byte[] oldData = file.getData().getBytes();

	// getFilePointer() returns position AFTER this current flush will be
	// completed
	int pos = (int) getFilePointer();
	int newDataLen = oldData.length;
	if (pos > newDataLen) {
	    // Resize buffer for new data past the end of old buffer
	    newDataLen = pos;
	}
	byte[] newData = new byte[newDataLen];
	System.arraycopy(oldData, 0, newData, 0, oldData.length);
	System.arraycopy(b, offset, newData, pos - len, len);

	file.setData(new Blob(newData));
	dao.save(file);
    }
}
