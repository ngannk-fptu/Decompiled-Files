/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.commons.NamespaceHelper;
import org.apache.jackrabbit.server.io.CopyMoveContext;
import org.apache.jackrabbit.server.io.CopyMoveHandler;
import org.apache.jackrabbit.server.io.DeleteContext;
import org.apache.jackrabbit.server.io.DeleteHandler;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHandler
implements IOHandler,
PropertyHandler,
CopyMoveHandler,
DeleteHandler {
    private static Logger log = LoggerFactory.getLogger(DefaultHandler.class);
    private String collectionNodetype;
    private String defaultNodetype;
    private String contentNodetype;
    private IOManager ioManager;

    public DefaultHandler() {
        this(null);
    }

    public DefaultHandler(IOManager ioManager) {
        this(ioManager, "nt:folder", "nt:file", "nt:unstructured");
    }

    public DefaultHandler(IOManager ioManager, String collectionNodetype, String defaultNodetype, String contentNodetype) {
        this.ioManager = ioManager;
        this.collectionNodetype = collectionNodetype;
        this.defaultNodetype = defaultNodetype;
        this.contentNodetype = contentNodetype;
    }

    @Override
    public IOManager getIOManager() {
        return this.ioManager;
    }

    @Override
    public void setIOManager(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean canImport(ImportContext context, boolean isCollection) {
        if (context == null || context.isCompleted()) {
            return false;
        }
        Item contextItem = context.getImportRoot();
        return contextItem != null && contextItem.isNode() && context.getSystemId() != null;
    }

    @Override
    public boolean canImport(ImportContext context, DavResource resource) {
        if (resource == null) {
            return false;
        }
        return this.canImport(context, resource.isCollection());
    }

    @Override
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        if (!this.canImport(context, isCollection)) {
            throw new IOException(this.getName() + ": Cannot import " + context.getSystemId());
        }
        boolean success = false;
        try {
            Node contentNode = this.getContentNode(context, isCollection);
            success = this.importData(context, isCollection, contentNode);
            if (success) {
                success = this.importProperties(context, isCollection, contentNode);
            }
        }
        catch (RepositoryException e) {
            success = false;
            throw new IOException(e.getMessage());
        }
        finally {
            if (!success) {
                try {
                    context.getImportRoot().refresh(false);
                }
                catch (RepositoryException e) {
                    throw new IOException(e.getMessage());
                }
            }
        }
        return success;
    }

    @Override
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        if (!this.canImport(context, resource)) {
            throw new IOException(this.getName() + ": Cannot import " + context.getSystemId());
        }
        return this.importContent(context, resource.isCollection());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean importData(ImportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        InputStream in = context.getInputStream();
        if (in != null) {
            if (isCollection) {
                return false;
            }
            try {
                contentNode.setProperty("jcr:data", in);
            }
            finally {
                in.close();
            }
        }
        return true;
    }

    protected boolean importProperties(ImportContext context, boolean isCollection, Node contentNode) {
        try {
            if (!contentNode.hasProperty("jcr:mimeType")) {
                contentNode.setProperty("jcr:mimeType", context.getMimeType());
            }
        }
        catch (RepositoryException repositoryException) {
            // empty catch block
        }
        try {
            if (!contentNode.hasProperty("jcr:encoding")) {
                contentNode.setProperty("jcr:encoding", context.getEncoding());
            }
        }
        catch (RepositoryException repositoryException) {
            // empty catch block
        }
        this.setLastModified(contentNode, context.getModificationTime());
        return true;
    }

    protected Node getContentNode(ImportContext context, boolean isCollection) throws RepositoryException {
        String name;
        Node parentNode = (Node)context.getImportRoot();
        if (parentNode.hasNode(name = context.getSystemId())) {
            parentNode = parentNode.getNode(name);
        } else {
            String ntName = isCollection ? this.getCollectionNodeType() : this.getNodeType();
            parentNode = parentNode.addNode(name, ntName);
        }
        Node contentNode = null;
        if (isCollection) {
            contentNode = parentNode;
        } else {
            if (parentNode.hasNode("jcr:content")) {
                contentNode = parentNode.getNode("jcr:content");
                if (contentNode.isNodeType(this.getContentNodeType()) || !this.forceCompatibleContentNodes()) {
                    if (contentNode.hasNodes()) {
                        NodeIterator it = contentNode.getNodes();
                        while (it.hasNext()) {
                            it.nextNode().remove();
                        }
                    }
                } else {
                    contentNode.remove();
                    contentNode = null;
                }
            }
            if (contentNode == null) {
                contentNode = parentNode.getPrimaryNodeType().canAddChildNode("jcr:content", this.getContentNodeType()) ? parentNode.addNode("jcr:content", this.getContentNodeType()) : parentNode.addNode("jcr:content");
            }
        }
        return contentNode;
    }

    protected boolean forceCompatibleContentNodes() {
        return false;
    }

    @Override
    public boolean canExport(ExportContext context, boolean isCollection) {
        boolean success;
        if (context == null || context.isCompleted()) {
            return false;
        }
        Item exportRoot = context.getExportRoot();
        boolean bl = success = exportRoot != null && exportRoot.isNode();
        if (success && !isCollection) {
            try {
                Node n = (Node)exportRoot;
                success = n.hasNode("jcr:content");
            }
            catch (RepositoryException e) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean canExport(ExportContext context, DavResource resource) {
        if (resource == null) {
            return false;
        }
        return this.canExport(context, resource.isCollection());
    }

    @Override
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
        if (!this.canExport(context, isCollection)) {
            throw new IOException(this.getName() + ": Cannot export " + context.getExportRoot());
        }
        try {
            Node contentNode = this.getContentNode(context, isCollection);
            this.exportProperties(context, isCollection, contentNode);
            if (context.hasStream()) {
                this.exportData(context, isCollection, contentNode);
            }
            return true;
        }
        catch (RepositoryException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
        if (!this.canExport(context, resource)) {
            throw new IOException(this.getName() + ": Cannot export " + context.getExportRoot());
        }
        return this.exportContent(context, resource.isCollection());
    }

    protected void exportData(ExportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
        if (contentNode.hasProperty("jcr:data")) {
            Property p = contentNode.getProperty("jcr:data");
            IOUtil.spool(p.getStream(), context.getOutputStream());
        }
    }

    protected void exportProperties(ExportContext context, boolean isCollection, Node contentNode) throws IOException {
        try {
            if (!isCollection && contentNode.getDepth() > 0 && contentNode.getParent().hasProperty("jcr:created")) {
                long cTime = contentNode.getParent().getProperty("jcr:created").getValue().getLong();
                context.setCreationTime(cTime);
            }
            long length = -1L;
            if (contentNode.hasProperty("jcr:data")) {
                Property p = contentNode.getProperty("jcr:data");
                length = p.getLength();
                context.setContentLength(length);
            }
            String mimeType = null;
            String encoding = null;
            if (contentNode.hasProperty("jcr:mimeType")) {
                mimeType = contentNode.getProperty("jcr:mimeType").getString();
            }
            if (contentNode.hasProperty("jcr:encoding") && "".equals(encoding = contentNode.getProperty("jcr:encoding").getString())) {
                encoding = null;
            }
            context.setContentType(mimeType, encoding);
            long modTime = -1L;
            if (contentNode.hasProperty("jcr:lastModified")) {
                modTime = contentNode.getProperty("jcr:lastModified").getLong();
                context.setModificationTime(modTime);
            } else {
                context.setModificationTime(System.currentTimeMillis());
            }
            if (length > -1L && modTime > -1L) {
                String etag = "\"" + length + "-" + modTime + "\"";
                context.setETag(etag);
            }
        }
        catch (RepositoryException e) {
            log.error("Unexpected error {} while exporting properties: {}", (Object)e.getClass().getName(), (Object)e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    protected Node getContentNode(ExportContext context, boolean isCollection) throws RepositoryException {
        Node contentNode = (Node)context.getExportRoot();
        if (!isCollection) {
            contentNode = contentNode.getNode("jcr:content");
        }
        return contentNode;
    }

    public String getCollectionNodeType() {
        return this.collectionNodetype;
    }

    public String getNodeType() {
        return this.defaultNodetype;
    }

    public String getContentNodeType() {
        return this.contentNodetype;
    }

    @Override
    public boolean canExport(PropertyExportContext context, boolean isCollection) {
        return this.canExport((ExportContext)context, isCollection);
    }

    @Override
    public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws RepositoryException {
        if (!this.canExport(exportContext, isCollection)) {
            throw new RepositoryException("PropertyHandler " + this.getName() + " failed to export properties.");
        }
        Node cn = this.getContentNode(exportContext, isCollection);
        try {
            this.exportProperties(exportContext, isCollection, cn);
            PropertyIterator it = cn.getProperties();
            while (it.hasNext()) {
                Property p = it.nextProperty();
                String name = p.getName();
                PropertyDefinition def = p.getDefinition();
                if (def.isMultiple() || DefaultHandler.isDefinedByFilteredNodeType(def)) {
                    log.debug("Skip property '" + name + "': not added to webdav property set.");
                    continue;
                }
                if ("jcr:data".equals(name) || "jcr:mimeType".equals(name) || "jcr:encoding".equals(name) || "jcr:lastModified".equals(name)) continue;
                DavPropertyName davName = this.getDavName(name, p.getSession());
                exportContext.setProperty(davName, p.getValue().getString());
            }
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean canImport(PropertyImportContext context, boolean isCollection) {
        if (context == null || context.isCompleted()) {
            return false;
        }
        Item contextItem = context.getImportRoot();
        try {
            return contextItem != null && contextItem.isNode() && (isCollection || ((Node)contextItem).hasNode("jcr:content"));
        }
        catch (RepositoryException e) {
            log.error("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<? extends PropEntry, ?> importProperties(PropertyImportContext importContext, boolean isCollection) throws RepositoryException {
        if (!this.canImport(importContext, isCollection)) {
            throw new RepositoryException("PropertyHandler " + this.getName() + " failed import properties");
        }
        HashMap<PropEntry, RepositoryException> failures = new HashMap<PropEntry, RepositoryException>();
        List<? extends PropEntry> changeList = importContext.getChangeList();
        Node cn = (Node)importContext.getImportRoot();
        if (!isCollection && cn.hasNode("jcr:content")) {
            cn = cn.getNode("jcr:content");
        }
        if (changeList != null) {
            for (PropEntry propEntry : changeList) {
                try {
                    if (propEntry instanceof DavPropertyName) {
                        DavPropertyName propName = (DavPropertyName)propEntry;
                        this.removeJcrProperty(propName, cn);
                        continue;
                    }
                    if (propEntry instanceof DavProperty) {
                        DavProperty prop = (DavProperty)propEntry;
                        this.setJcrProperty(prop, cn);
                        continue;
                    }
                    log.error("unknown object in change list: " + propEntry.getClass().getName());
                }
                catch (RepositoryException e) {
                    failures.put(propEntry, e);
                }
            }
        }
        if (failures.isEmpty()) {
            this.setLastModified(cn, -1L);
        }
        return failures;
    }

    protected String detect(String name) {
        try {
            Metadata metadata = new Metadata();
            metadata.set("resourceName", name);
            if (this.ioManager != null && this.ioManager.getDetector() != null) {
                return this.ioManager.getDetector().detect(null, metadata).toString();
            }
            return "application/octet-stream";
        }
        catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }

    @Override
    public boolean canCopy(CopyMoveContext context, DavResource source, DavResource destination) {
        return true;
    }

    @Override
    public boolean copy(CopyMoveContext context, DavResource source, DavResource destination) throws DavException {
        if (context.isShallowCopy() && source.isCollection()) {
            throw new DavException(403, "Unable to perform shallow copy.");
        }
        try {
            context.getSession().getWorkspace().copy(source.getLocator().getRepositoryPath(), destination.getLocator().getRepositoryPath());
            return true;
        }
        catch (PathNotFoundException e) {
            throw new DavException(409, e.getMessage());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public boolean canMove(CopyMoveContext context, DavResource source, DavResource destination) {
        return true;
    }

    @Override
    public boolean move(CopyMoveContext context, DavResource source, DavResource destination) throws DavException {
        try {
            context.getWorkspace().move(source.getLocator().getRepositoryPath(), destination.getLocator().getRepositoryPath());
            return true;
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public boolean canDelete(DeleteContext deleteContext, DavResource member) {
        return true;
    }

    @Override
    public boolean delete(DeleteContext deleteContext, DavResource member) throws DavException {
        try {
            String itemPath = member.getLocator().getRepositoryPath();
            Item item = deleteContext.getSession().getItem(itemPath);
            if (item instanceof Node) {
                ((Node)item).removeShare();
            } else {
                item.remove();
            }
            deleteContext.getSession().save();
            log.debug("default handler deleted {}", (Object)member.getResourcePath());
            return true;
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    private DavPropertyName getDavName(String jcrName, Session session) throws RepositoryException {
        String localName = ISO9075.encode(Text.getLocalName(jcrName));
        String prefix = Text.getNamespacePrefix(jcrName);
        String uri = session.getNamespaceURI(prefix);
        Namespace namespace = Namespace.getNamespace(prefix, uri);
        DavPropertyName name = DavPropertyName.create(localName, namespace);
        return name;
    }

    private String getJcrName(DavPropertyName propName, Session session) throws RepositoryException {
        String pName = ISO9075.decode(propName.getName());
        Namespace propNamespace = propName.getNamespace();
        if (!Namespace.EMPTY_NAMESPACE.equals(propNamespace)) {
            NamespaceHelper helper = new NamespaceHelper(session);
            String prefix = helper.registerNamespace(propNamespace.getPrefix(), propNamespace.getURI());
            pName = prefix + ":" + pName;
        }
        return pName;
    }

    private void setJcrProperty(DavProperty<?> property, Node contentNode) throws RepositoryException {
        DavPropertyName davName;
        String value = "";
        if (property.getValue() != null) {
            value = property.getValue().toString();
        }
        if (DavPropertyName.GETCONTENTTYPE.equals(davName = property.getName())) {
            String mimeType = IOUtil.getMimeType(value);
            String encoding = IOUtil.getEncoding(value);
            contentNode.setProperty("jcr:mimeType", mimeType);
            contentNode.setProperty("jcr:encoding", encoding);
        } else {
            contentNode.setProperty(this.getJcrName(davName, contentNode.getSession()), value);
        }
    }

    private void removeJcrProperty(DavPropertyName propertyName, Node contentNode) throws RepositoryException {
        if (DavPropertyName.GETCONTENTTYPE.equals(propertyName)) {
            if (contentNode.hasProperty("jcr:mimeType")) {
                contentNode.getProperty("jcr:mimeType").remove();
            }
            if (contentNode.hasProperty("jcr:encoding")) {
                contentNode.getProperty("jcr:encoding").remove();
            }
        } else {
            String jcrName = this.getJcrName(propertyName, contentNode.getSession());
            if (contentNode.hasProperty(jcrName)) {
                contentNode.getProperty(jcrName).remove();
            }
        }
    }

    private void setLastModified(Node contentNode, long hint) {
        try {
            Calendar lastMod = Calendar.getInstance();
            if (hint > -1L) {
                lastMod.setTimeInMillis(hint);
            } else {
                lastMod.setTime(new Date());
            }
            contentNode.setProperty("jcr:lastModified", lastMod);
        }
        catch (RepositoryException repositoryException) {
            // empty catch block
        }
    }

    private static boolean isDefinedByFilteredNodeType(PropertyDefinition def) {
        String ntName = def.getDeclaringNodeType().getName();
        return ntName.equals("nt:base") || ntName.equals("mix:referenceable") || ntName.equals("mix:versionable") || ntName.equals("mix:lockable");
    }

    public void setCollectionNodetype(String collectionNodetype) {
        this.collectionNodetype = collectionNodetype;
    }

    public void setDefaultNodetype(String defaultNodetype) {
        this.defaultNodetype = defaultNodetype;
    }

    public void setContentNodetype(String contentNodetype) {
        this.contentNodetype = contentNodetype;
    }
}

