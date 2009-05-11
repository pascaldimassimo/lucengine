package lucengine.core;

import java.util.List;

public interface IndexFileDao {

    void save(IndexFile file);
    
    void delete(IndexFile file);
    
    IndexFile getByFilename(String filename);
    
    List<IndexFile> getFiles();
    
}
