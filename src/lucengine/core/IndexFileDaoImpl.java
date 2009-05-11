package lucengine.core;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.orm.jdo.support.JdoDaoSupport;

public class IndexFileDaoImpl extends JdoDaoSupport implements IndexFileDao {

    @Override
    public void delete(IndexFile file) {
	getJdoTemplate().deletePersistent(file);
    }

    @Override
    public IndexFile getByFilename(String filename) {
	// TODO should work with:
	// getJdoTemplate().find(...)

	PersistenceManager pm = getPersistenceManager();
	try {
	    Query query = pm.newQuery(IndexFile.class);
	    query.setFilter("filename == pfilename");
	    query.declareParameters("java.lang.String pfilename");
	    List<IndexFile> files = (List<IndexFile>) query.execute(filename);
	    if (files.size() > 0) {
		return pm.detachCopy(files.get(0));
	    }
	} finally {
	    pm.close();
	}
	return null;
    }

    @Override
    public List<IndexFile> getFiles() {
	PersistenceManager pm = getPersistenceManager();
	try {
	    Query query = pm.newQuery(IndexFile.class);
	    List<IndexFile> files = (List<IndexFile>) query.execute();
	    return (List<IndexFile>) pm.detachCopyAll(files);
	} finally {
	    pm.close();
	}
    }

    @Override
    public void save(IndexFile file) {
	getJdoTemplate().makePersistent(file);
    }
    
    public IndexFile saveAndDetach(IndexFile file) {
	getJdoTemplate().makePersistent(file);
	PersistenceManager pm = getPersistenceManager();
	IndexFile detachCopy = pm.detachCopy(file);
	return detachCopy;
    }

}
