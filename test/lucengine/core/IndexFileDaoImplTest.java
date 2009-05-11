package lucengine.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.api.datastore.Blob;

public class IndexFileDaoImplTest extends JdoTest {

    @Test
    public void update_are_saved() {
	IndexFile file = createFile();

	file.setFilename("toto");
	file.setData(new Blob(new byte[] { 0xB, 0xA, 0xB, 0xE }));
	dao.save(file);

	IndexFile file2 = dao.getByFilename(file.getFilename());
	Assert.assertEquals(file, file2);
    }

    @Test
    public void delete_works() {
	IndexFile file = createFile();
	dao.delete(file);
	Assert.assertNull(dao.getByFilename(file.getFilename()));
    }
    
    @Test
    public void list_works() {
	List<IndexFile> files = dao.getFiles();
	Assert.assertEquals(0, files.size());
	IndexFile file = createFile();
	files = dao.getFiles();
	Assert.assertEquals(1, files.size());
	Assert.assertEquals(file, files.get(0));
    }
    
    @Test
    public void get_by_filename_works_after_rename() {
	IndexFile file = createFile();
	String from = file.getFilename();
	Assert.assertNotNull(dao.getByFilename(from));
	
	file.setFilename(from + "_bak");
	dao.save(file);
	Assert.assertNull(dao.getByFilename(from));
    }
    
    //@Test
    public void multiple_save() {
	IndexFile file = new IndexFile();
	file.setFilename("1");
	dao.saveAndDetach(file);
	
	file.setFilename("2");
	dao.save(file);
    }
}
