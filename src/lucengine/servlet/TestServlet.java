package lucengine.servlet;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lucengine.core.IndexFileDaoImpl;
import lucengine.core.JdoDirectory;
import lucengine.core.PMF;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.NoLockFactory;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	try {
	    IndexFileDaoImpl dao = new IndexFileDaoImpl();
	    dao.setPersistenceManagerFactory(PMF.get());

	    JdoDirectory dir = new JdoDirectory(dao);
	    dir.setLockFactory(NoLockFactory.getNoLockFactory());

	    IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(),
	    	IndexWriter.MaxFieldLength.LIMITED);
	    writer.setMergeScheduler(new SerialMergeScheduler());

	    Document doc = new Document();
	    String text = "The Spring Framework provides a mechanism for designing systems, aiding you in the modularisation of components and hence in making components more readily testable. The crux of the framework is that you design your business service and data access objects as Java beans, and then provide a dependency mapping between them. This leads to very well structured systems with a pluggable feel.";
	    doc.add(new Field("contents", new StringReader(text)));
	    doc.add(new Field("filename", "spring", Field.Store.YES, Field.Index.NOT_ANALYZED));

	    writer.addDocument(doc);
	    resp.getWriter().println("Indexing... DONE");
	    writer.commit();
	    resp.getWriter().println("Committing... DONE");
	    writer.optimize();
	    resp.getWriter().println("Optimizing... DONE");

	    IndexSearcher is = new IndexSearcher(dir);
	    QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
	    Query query = parser.parse("systems");
	    is.search(query, 10);
	    resp.getWriter().println("Searching... DONE");
	    
	    writer.deleteDocuments(parser.parse("filename:spring"));
	    writer.commit();
	    resp.getWriter().println("Deleting... DONE");
	    
	} catch (ParseException e) {
	    throw new RuntimeException(e);
	}
    }
}
