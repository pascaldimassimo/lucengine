package lucengine.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

public class JdoDirectory extends Directory {

    IndexFileDao dao;

    public JdoDirectory(IndexFileDao dao) {
	this.dao = dao;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public IndexOutput createOutput(String filename) throws IOException {
	IndexFile file = dao.getByFilename(filename);
	if (file == null) {
	    file = createFile(filename);
	}
	return new JdoIndexOutput(file, dao);
    }

    @Override
    public void deleteFile(String filename) throws IOException {
	IndexFile file = dao.getByFilename(filename);
	dao.delete(file);
    }

    @Override
    public boolean fileExists(String filename) throws IOException {
	return dao.getByFilename(filename) != null;
    }

    @Override
    public long fileLength(String filename) throws IOException {
	IndexFile file = dao.getByFilename(filename);
	if (file == null) {
	    throw new IOException(String.format("File %s doesn't exists", filename));
	}
	return file.getData().getBytes().length;
    }

    @Override
    public long fileModified(String arg0) throws IOException {
	// TODO add a timestamp of the last modif in IndexFile
	return 0;
    }

    @Override
    public String[] list() throws IOException {
	List<IndexFile> files = dao.getFiles();
	String[] filenames = new String[files.size()];
	for (int i = 0; i < files.size(); i++) {
	    filenames[i] = files.get(i).getFilename();
	}
	return filenames;
    }

    @Override
    public IndexInput openInput(String filename) throws IOException {
	IndexFile file = dao.getByFilename(filename);
	if (file == null) {
	    throw new FileNotFoundException(filename);
	}
	return new JdoIndexInput(file, dao);
    }

    @Override
    public void renameFile(String from, String to) throws IOException {
	IndexFile file = dao.getByFilename(from);
	file.setFilename(to);
	dao.save(file);
    }

    @Override
    public void touchFile(String arg0) throws IOException {
	// TODO add a timestamp of the last modif in IndexFile
    }

    private IndexFile createFile(String filename) {
	IndexFile file;
	file = new IndexFile();
	file.setFilename(filename);
	dao.save(file);
	// TODO could we just return the file???
	return dao.getByFilename(file.getFilename());
	// return file;
    }
}
