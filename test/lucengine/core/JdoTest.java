package lucengine.core;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

public class JdoTest {

    IndexFileDaoImpl dao;

    public JdoTest() {
	super();
    }

    @Before
    public void setup() {
	ApiProxy.setEnvironmentForCurrentThread(new Environment());
	ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
	});
	ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
	proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());

	dao = new IndexFileDaoImpl();
	dao.setPersistenceManagerFactory(PMF.get());
    }

    @After
    public void tearDown() throws Exception {
	ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
	LocalDatastoreService datastoreService = (LocalDatastoreService) proxy
		.getService("datastore_v3");
	datastoreService.clearProfiles();
    }

    protected IndexFile createFile() {
	IndexFile file = new IndexFile();
	file.setFilename("01.fnm");
	file.setData(new Blob(new byte[] { 0xC, 0xA, 0xF, 0xE }));
	dao.save(file);
	return dao.getByFilename(file.getFilename());
    }

    protected IndexFile createEmptyFile() {
	IndexFile file = new IndexFile();
	file.setFilename("01.fnm");
	dao.save(file);
	return dao.getByFilename(file.getFilename());
    }

}