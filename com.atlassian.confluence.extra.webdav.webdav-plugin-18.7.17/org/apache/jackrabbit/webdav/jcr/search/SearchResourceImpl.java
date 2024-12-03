/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.search;

import java.util.ArrayList;
import java.util.Map;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.search.SearchResultProperty;
import org.apache.jackrabbit.webdav.search.QueryGrammerSet;
import org.apache.jackrabbit.webdav.search.SearchInfo;
import org.apache.jackrabbit.webdav.search.SearchResource;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchResourceImpl
implements SearchResource {
    private static Logger log = LoggerFactory.getLogger(SearchResourceImpl.class);
    private final JcrDavSession session;
    private final DavResourceLocator locator;

    public SearchResourceImpl(DavResourceLocator locator, JcrDavSession session) {
        this.session = session;
        this.locator = locator;
    }

    @Override
    public QueryGrammerSet getQueryGrammerSet() {
        QueryGrammerSet qgs = new QueryGrammerSet();
        try {
            String[] langs;
            QueryManager qMgr = this.getRepositorySession().getWorkspace().getQueryManager();
            for (String lang : langs = qMgr.getSupportedQueryLanguages()) {
                qgs.addQueryLanguage(lang, Namespace.EMPTY_NAMESPACE);
            }
        }
        catch (RepositoryException e) {
            log.debug(e.getMessage());
        }
        return qgs;
    }

    @Override
    public MultiStatus search(SearchInfo sInfo) throws DavException {
        try {
            QueryResult result = this.getQuery(sInfo).execute();
            MultiStatus ms = new MultiStatus();
            if (ItemResourceConstants.NAMESPACE.equals(sInfo.getLanguageNameSpace())) {
                ms.setResponseDescription("Columns: " + this.encode(result.getColumnNames()) + "\nSelectors: " + this.encode(result.getSelectorNames()));
            } else {
                ms.setResponseDescription(this.encode(result.getColumnNames()));
            }
            this.queryResultToMultiStatus(result, ms);
            return ms;
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Query getQuery(SearchInfo sInfo) throws InvalidQueryException, RepositoryException, DavException {
        Query q;
        String itemPath;
        block16: {
            block15: {
                Node qNode;
                String qNodeRelPath;
                Session session = this.getRepositorySession();
                NamespaceRegistry nsReg = session.getWorkspace().getNamespaceRegistry();
                Node rootNode = session.getRootNode();
                QueryManager qMgr = this.getRepositorySession().getWorkspace().getQueryManager();
                itemPath = this.locator.getRepositoryPath();
                if (itemPath != null && !rootNode.getPath().equals(itemPath) && rootNode.hasNode(qNodeRelPath = itemPath.substring(1)) && (qNode = rootNode.getNode(qNodeRelPath)).isNodeType("nt:query")) {
                    return qMgr.getQuery(qNode);
                }
                if (sInfo == null) break block15;
                Map<String, String> namespaces = sInfo.getNamespaces();
                try {
                    for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                        String prefix = entry.getKey();
                        String uri = entry.getValue();
                        session.setNamespacePrefix(prefix, uri);
                    }
                    q = qMgr.createQuery(sInfo.getQuery(), sInfo.getLanguageName());
                    if (-1L != sInfo.getNumberResults()) {
                        q.setLimit(sInfo.getNumberResults());
                    }
                    if (-1L != sInfo.getOffset()) {
                        q.setOffset(sInfo.getOffset());
                    }
                }
                catch (Throwable throwable) {
                    for (String uri : namespaces.values()) {
                        try {
                            session.setNamespacePrefix(nsReg.getPrefix(uri), uri);
                        }
                        catch (RepositoryException e) {
                            log.warn("Unable to reset mapping of namespace: " + uri);
                        }
                    }
                    throw throwable;
                }
                for (String uri : namespaces.values()) {
                    try {
                        session.setNamespacePrefix(nsReg.getPrefix(uri), uri);
                    }
                    catch (RepositoryException e) {
                        log.warn("Unable to reset mapping of namespace: " + uri);
                    }
                }
                break block16;
            }
            throw new DavException(400, this.locator.getResourcePath() + " is not a nt:query node -> searchRequest body required.");
        }
        if (itemPath != null && !this.getRepositorySession().itemExists(itemPath)) {
            try {
                q.storeAsNode(itemPath);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }
        return q;
    }

    private void queryResultToMultiStatus(QueryResult result, MultiStatus ms) throws RepositoryException {
        ArrayList<String> columnNames = new ArrayList<String>();
        ValueFactory vf = this.getRepositorySession().getValueFactory();
        ArrayList<RowValue> descr = new ArrayList<RowValue>();
        for (String columnName : result.getColumnNames()) {
            if (SearchResourceImpl.isPathOrScore(columnName)) continue;
            columnNames.add(columnName);
            descr.add(new PlainValue(columnName, null, vf));
        }
        String[] sns = result.getSelectorNames();
        boolean join = sns.length > 1;
        for (String selectorName : sns) {
            descr.add(new PathValue("jcr:path", selectorName, vf));
            columnNames.add("jcr:path");
            descr.add(new ScoreValue("jcr:score", selectorName, vf));
            columnNames.add("jcr:score");
        }
        int n = 0;
        String root = this.getHref("/");
        String[] selectorNames = SearchResourceImpl.createSelectorNames(descr);
        String[] colNames = columnNames.toArray(new String[columnNames.size()]);
        RowIterator rowIter = result.getRows();
        while (rowIter.hasNext()) {
            Row row = rowIter.nextRow();
            ArrayList<Value> values = new ArrayList<Value>();
            for (RowValue rv : descr) {
                values.add(rv.getValue(row));
            }
            String href = join ? root + "?" + n++ : this.getHref(row.getPath());
            MultiStatusResponse resp = new MultiStatusResponse(href, null);
            SearchResultProperty srp = new SearchResultProperty(colNames, selectorNames, values.toArray(new Value[values.size()]));
            resp.add(srp);
            ms.addResponse(resp);
        }
    }

    private String getHref(String path) throws RepositoryException {
        DavResourceLocator l = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), path, false);
        return l.getHref(true);
    }

    private String encode(String[] names) {
        StringBuilder builder = new StringBuilder();
        String delim = "";
        for (String name : names) {
            builder.append(delim);
            builder.append(ISO9075.encode(name));
            delim = " ";
        }
        return builder.toString();
    }

    private static String[] createSelectorNames(Iterable<RowValue> rows) throws RepositoryException {
        ArrayList<String> sn = new ArrayList<String>();
        for (RowValue rv : rows) {
            sn.add(rv.getSelectorName());
        }
        return sn.toArray(new String[sn.size()]);
    }

    private static boolean isPathOrScore(String columnName) {
        return "jcr:path".equals(columnName) || "jcr:score".equals(columnName);
    }

    private Session getRepositorySession() {
        return this.session.getRepositorySession();
    }

    private static final class PathValue
    extends SelectorValue {
        public PathValue(String columnName, String selectorName, ValueFactory vf) {
            super(columnName, selectorName, vf);
        }

        @Override
        public Value getValue(Row row) throws RepositoryException {
            String path = this.selectorName != null ? row.getPath(this.selectorName) : row.getPath();
            return path == null ? null : this.vf.createValue(path, 8);
        }
    }

    private static final class ScoreValue
    extends SelectorValue {
        public ScoreValue(String columnName, String selectorName, ValueFactory vf) {
            super(columnName, selectorName, vf);
        }

        @Override
        public Value getValue(Row row) throws RepositoryException {
            double score = this.selectorName != null ? row.getScore(this.selectorName) : row.getScore();
            return this.vf.createValue(score);
        }
    }

    private static abstract class SelectorValue
    implements RowValue {
        protected final String columnName;
        protected final String selectorName;
        protected final ValueFactory vf;

        public SelectorValue(String columnName, String selectorName, ValueFactory vf) {
            this.columnName = columnName;
            this.selectorName = selectorName;
            this.vf = vf;
        }

        @Override
        public String getColumnName() throws RepositoryException {
            return this.columnName;
        }

        @Override
        public String getSelectorName() throws RepositoryException {
            return this.selectorName;
        }
    }

    private static final class PlainValue
    extends SelectorValue {
        public PlainValue(String columnName, String selectorName, ValueFactory vf) {
            super(columnName, selectorName, vf);
        }

        @Override
        public Value getValue(Row row) throws RepositoryException {
            return row.getValue(this.columnName);
        }
    }

    private static interface RowValue {
        public Value getValue(Row var1) throws RepositoryException;

        public String getColumnName() throws RepositoryException;

        public String getSelectorName() throws RepositoryException;
    }
}

