/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.jcr.Credentials;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.commons.xml.DocumentViewExporter;
import org.apache.jackrabbit.commons.xml.Exporter;
import org.apache.jackrabbit.commons.xml.ParsingContentHandler;
import org.apache.jackrabbit.commons.xml.SystemViewExporter;
import org.apache.jackrabbit.commons.xml.ToXmlContentHandler;
import org.apache.jackrabbit.util.XMLChar;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class AbstractSession
implements Session {
    private final Map<String, String> namespaces = new HashMap<String, String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void logout() {
        Map<String, String> map = this.namespaces;
        synchronized (map) {
            this.namespaces.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getNamespacePrefix(String uri) throws NamespaceException, RepositoryException {
        Map<String, String> map = this.namespaces;
        synchronized (map) {
            String prefix;
            for (Map.Entry<String, String> entry : this.namespaces.entrySet()) {
                if (!entry.getValue().equals(uri)) continue;
                return entry.getKey();
            }
            String base = prefix = this.getWorkspace().getNamespaceRegistry().getPrefix(uri);
            int i = 2;
            while (this.namespaces.containsKey(prefix)) {
                prefix = base + i;
                ++i;
            }
            this.namespaces.put(prefix, uri);
            return prefix;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getNamespaceURI(String prefix) throws NamespaceException, RepositoryException {
        Map<String, String> map = this.namespaces;
        synchronized (map) {
            String uri = this.namespaces.get(prefix);
            if (uri == null) {
                uri = this.getWorkspace().getNamespaceRegistry().getURI(prefix);
                if (this.namespaces.containsValue(uri)) {
                    throw new NamespaceException("Namespace not found: " + prefix);
                }
                this.namespaces.put(prefix, uri);
            }
            return uri;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getNamespacePrefixes() throws RepositoryException {
        for (String uri : this.getWorkspace().getNamespaceRegistry().getURIs()) {
            this.getNamespacePrefix(uri);
        }
        Map<String, String> map = this.namespaces;
        synchronized (map) {
            return this.namespaces.keySet().toArray(new String[this.namespaces.size()]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setNamespacePrefix(String prefix, String uri) throws NamespaceException, RepositoryException {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix must not be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Namespace must not be null");
        }
        if (prefix.length() == 0) {
            throw new NamespaceException("Empty prefix is reserved and can not be remapped");
        }
        if (uri.length() == 0) {
            throw new NamespaceException("Default namespace is reserved and can not be remapped");
        }
        if (prefix.toLowerCase().startsWith("xml")) {
            throw new NamespaceException("XML prefixes are reserved: " + prefix);
        }
        if (!XMLChar.isValidNCName(prefix)) {
            throw new NamespaceException("Prefix is not a valid XML NCName: " + prefix);
        }
        Map<String, String> map = this.namespaces;
        synchronized (map) {
            this.namespaces.remove(prefix);
            HashSet<String> prefixes = new HashSet<String>();
            for (Map.Entry<String, String> entry : this.namespaces.entrySet()) {
                if (!entry.getValue().equals(uri)) continue;
                prefixes.add(entry.getKey());
            }
            this.namespaces.keySet().removeAll(prefixes);
            this.namespaces.put(prefix, uri);
        }
    }

    @Override
    public void exportDocumentView(String path, ContentHandler handler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {
        this.export(path, new DocumentViewExporter(this, handler, !noRecurse, !skipBinary));
    }

    @Override
    public void exportSystemView(String path, ContentHandler handler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {
        this.export(path, new SystemViewExporter(this, handler, !noRecurse, !skipBinary));
    }

    @Override
    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, RepositoryException {
        try {
            ToXmlContentHandler handler = new ToXmlContentHandler(out);
            this.exportDocumentView(absPath, handler, skipBinary, noRecurse);
        }
        catch (SAXException e) {
            Exception exception = e.getException();
            if (exception instanceof RepositoryException) {
                throw (RepositoryException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new RepositoryException("Error serializing document view XML", e);
        }
    }

    @Override
    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, RepositoryException {
        try {
            ToXmlContentHandler handler = new ToXmlContentHandler(out);
            this.exportSystemView(absPath, handler, skipBinary, noRecurse);
        }
        catch (SAXException e) {
            Exception exception = e.getException();
            if (exception instanceof RepositoryException) {
                throw (RepositoryException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new RepositoryException("Error serializing system view XML", e);
        }
    }

    @Override
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, InvalidSerializedDataException, RepositoryException {
        try {
            ContentHandler handler = this.getImportContentHandler(parentAbsPath, uuidBehavior);
            new ParsingContentHandler(handler).parse(in);
        }
        catch (SAXException e) {
            Exception exception = e.getException();
            if (exception instanceof RepositoryException) {
                throw (RepositoryException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new InvalidSerializedDataException("XML parse error", e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private String toRelativePath(String absPath) throws PathNotFoundException {
        if (absPath.startsWith("/") && absPath.length() > 1) {
            return absPath.substring(1);
        }
        throw new PathNotFoundException("Not an absolute path: " + absPath);
    }

    @Override
    public Item getItem(String absPath) throws PathNotFoundException, RepositoryException {
        Node root = this.getRootNode();
        if (absPath.equals("/")) {
            return root;
        }
        if (absPath.startsWith("[") && absPath.endsWith("]")) {
            return this.getNodeByIdentifier(absPath.substring(1, absPath.length() - 1));
        }
        String relPath = this.toRelativePath(absPath);
        if (root.hasNode(relPath)) {
            return root.getNode(relPath);
        }
        return root.getProperty(relPath);
    }

    @Override
    public boolean itemExists(String absPath) throws RepositoryException {
        String relPath;
        if (absPath.equals("/")) {
            return true;
        }
        Node root = this.getRootNode();
        return root.hasNode(relPath = this.toRelativePath(absPath)) || root.hasProperty(relPath);
    }

    @Override
    public void removeItem(String absPath) throws RepositoryException {
        this.getItem(absPath).remove();
    }

    @Override
    public Node getNode(String absPath) throws RepositoryException {
        Node root = this.getRootNode();
        if (absPath.equals("/")) {
            return root;
        }
        return root.getNode(this.toRelativePath(absPath));
    }

    @Override
    public boolean nodeExists(String absPath) throws RepositoryException {
        if (absPath.equals("/")) {
            return true;
        }
        return this.getRootNode().hasNode(this.toRelativePath(absPath));
    }

    @Override
    public Property getProperty(String absPath) throws RepositoryException {
        if (absPath.equals("/")) {
            throw new RepositoryException("The root node is not a property");
        }
        return this.getRootNode().getProperty(this.toRelativePath(absPath));
    }

    @Override
    public boolean propertyExists(String absPath) throws RepositoryException {
        if (absPath.equals("/")) {
            return false;
        }
        return this.getRootNode().hasProperty(this.toRelativePath(absPath));
    }

    @Override
    public Session impersonate(Credentials credentials) throws RepositoryException {
        return this.getRepository().login(credentials, this.getWorkspace().getName());
    }

    private synchronized void export(String path, Exporter exporter) throws PathNotFoundException, SAXException, RepositoryException {
        Item item = this.getItem(path);
        if (!item.isNode()) {
            throw new PathNotFoundException("XML export is not defined for properties: " + path);
        }
        exporter.export((Node)item);
    }
}

