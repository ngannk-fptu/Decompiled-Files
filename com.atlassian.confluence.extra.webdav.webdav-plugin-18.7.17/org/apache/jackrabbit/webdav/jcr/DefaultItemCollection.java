/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.commons.webdav.JcrValueType;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.AbstractItemResource;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.lock.JcrActiveLock;
import org.apache.jackrabbit.webdav.jcr.lock.SessionScopedLockEntry;
import org.apache.jackrabbit.webdav.jcr.nodetype.NodeTypeProperty;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.jcr.property.ValuesProperty;
import org.apache.jackrabbit.webdav.jcr.security.JcrSupportedPrivilegesProperty;
import org.apache.jackrabbit.webdav.jcr.security.JcrUserPrivilegesProperty;
import org.apache.jackrabbit.webdav.jcr.security.SecurityUtils;
import org.apache.jackrabbit.webdav.jcr.version.report.ExportViewReport;
import org.apache.jackrabbit.webdav.jcr.version.report.LocateCorrespondingNodeReport;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;
import org.apache.jackrabbit.webdav.ordering.OrderingConstants;
import org.apache.jackrabbit.webdav.ordering.OrderingResource;
import org.apache.jackrabbit.webdav.ordering.OrderingType;
import org.apache.jackrabbit.webdav.ordering.Position;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.util.HttpDateFormat;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DefaultItemCollection
extends AbstractItemResource
implements OrderingResource {
    private static Logger log = LoggerFactory.getLogger(DefaultItemCollection.class);
    private static final String TMP_PREFIX = "_tmp_";

    protected DefaultItemCollection(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory, Item item) {
        super(locator, session, factory, item);
        if (this.exists() && !(item instanceof Node)) {
            throw new IllegalArgumentException("A collection resource can not be constructed from a Property item.");
        }
    }

    @Override
    public String getComplianceClass() {
        String cc = super.getComplianceClass();
        if (this.isOrderable()) {
            return DavCompliance.concatComplianceClasses(new String[]{cc, "ordered-collections"});
        }
        return cc;
    }

    @Override
    public long getModificationTime() {
        if (this.exists()) {
            try {
                if (((Node)this.item).hasProperty("jcr:lastModified")) {
                    return ((Node)this.item).getProperty("jcr:lastModified").getLong();
                }
            }
            catch (RepositoryException e) {
                log.warn("Error while accessing jcr:lastModified property");
            }
        }
        return new Date().getTime();
    }

    @Override
    public String getSupportedMethods() {
        String ms = super.getSupportedMethods();
        if (this.isOrderable()) {
            StringBuffer sb = new StringBuffer(ms);
            sb.append(", ").append("ORDERPATCH");
            return sb.toString();
        }
        return ms;
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        super.spool(outputContext);
        try {
            OutputStream out = outputContext.getOutputStream();
            if (out != null && this.exists()) {
                this.getRepositorySession().exportSystemView(this.item.getPath(), out, false, true);
            }
        }
        catch (PathNotFoundException e) {
            log.error("Error while spooling resource content: " + e.getMessage());
        }
        catch (RepositoryException e) {
            log.error("Error while spooling resource content: " + e.getMessage());
        }
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        AbstractDavProperty prop = super.getProperty(name);
        if (prop == null && this.exists()) {
            Node n = (Node)this.item;
            try {
                if (JCR_INDEX.equals(name)) {
                    prop = new DefaultDavProperty<Integer>(JCR_INDEX, Integer.valueOf(n.getIndex()), true);
                } else if (JCR_REFERENCES.equals(name)) {
                    prop = this.getHrefProperty(JCR_REFERENCES, n.getReferences(), true);
                } else if (JCR_WEAK_REFERENCES.equals(name)) {
                    prop = this.getHrefProperty(JCR_WEAK_REFERENCES, n.getWeakReferences(), true);
                } else if (JCR_UUID.equals(name)) {
                    if (this.isReferenceable()) {
                        prop = new DefaultDavProperty<String>(JCR_UUID, n.getUUID(), true);
                    }
                } else if (JCR_PRIMARYITEM.equals(name)) {
                    if (this.hasPrimaryItem()) {
                        Item primaryItem = n.getPrimaryItem();
                        prop = this.getHrefProperty(JCR_PRIMARYITEM, new Item[]{primaryItem}, true);
                    }
                } else if (OrderingConstants.ORDERING_TYPE.equals(name) && this.isOrderable()) {
                    prop = new OrderingType("DAV:custom");
                } else if (SecurityConstants.SUPPORTED_PRIVILEGE_SET.equals(name)) {
                    prop = new JcrSupportedPrivilegesProperty(this.getRepositorySession(), n.getPath()).asDavProperty();
                } else if (SecurityConstants.CURRENT_USER_PRIVILEGE_SET.equals(name)) {
                    prop = new JcrUserPrivilegesProperty(this.getRepositorySession(), n.getPath()).asDavProperty();
                }
            }
            catch (RepositoryException e) {
                log.error("Failed to retrieve node-specific property: " + e);
            }
        }
        return prop;
    }

    @Override
    public void setProperty(DavProperty<?> property) throws DavException {
        this.internalSetProperty(property);
        this.complete();
    }

    private void internalSetProperty(DavProperty<?> property) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        DavPropertyName propName = property.getName();
        if (JCR_MIXINNODETYPES.equals(propName)) {
            Node n = (Node)this.item;
            try {
                NodeTypeProperty mix = new NodeTypeProperty(property);
                Set<String> mixins = mix.getNodeTypeNames();
                for (NodeType existingMixin : n.getMixinNodeTypes()) {
                    String name = existingMixin.getName();
                    if (mixins.contains(name)) {
                        mixins.remove(name);
                        continue;
                    }
                    n.removeMixin(name);
                }
                for (String mixin : mixins) {
                    n.addMixin(mixin);
                }
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else if (JCR_PRIMARYNODETYPE.equals(propName)) {
            Node n = (Node)this.item;
            try {
                NodeTypeProperty ntProp = new NodeTypeProperty(property);
                Set<String> names = ntProp.getNodeTypeNames();
                if (names.size() != 1) {
                    throw new DavException(400);
                }
                String ntName = names.iterator().next();
                n.setPrimaryType(ntName);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            throw new DavException(409);
        }
    }

    @Override
    public void removeProperty(DavPropertyName propertyName) throws DavException {
        this.internalRemoveProperty(propertyName);
        this.complete();
    }

    private void internalRemoveProperty(DavPropertyName propertyName) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (JCR_MIXINNODETYPES.equals(propertyName)) {
            try {
                Node n = (Node)this.item;
                for (NodeType mixin : n.getMixinNodeTypes()) {
                    n.removeMixin(mixin.getName());
                }
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            throw new DavException(409);
        }
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        for (PropEntry propEntry : changeList) {
            if (propEntry instanceof DavPropertyName) {
                DavPropertyName propName = (DavPropertyName)propEntry;
                this.internalRemoveProperty(propName);
                continue;
            }
            if (propEntry instanceof DavProperty) {
                DavProperty prop = (DavProperty)propEntry;
                this.internalSetProperty(prop);
                continue;
            }
            throw new IllegalArgumentException("unknown object in change list: " + propEntry.getClass().getName());
        }
        this.complete();
        return new MultiStatusResponse(this.getHref(), 200);
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        if (!this.exists()) {
            throw new DavException(409);
        }
        File tmpFile = null;
        try {
            Node n = (Node)this.item;
            InputStream in = inputContext != null ? inputContext.getInputStream() : null;
            String itemPath = this.getLocator().getRepositoryPath();
            String memberName = DefaultItemCollection.getItemName(resource.getLocator().getRepositoryPath());
            if (resource.isCollection()) {
                if (in == null) {
                    n.addNode(memberName);
                } else {
                    int uuidBehavior = 0;
                    String str = inputContext.getProperty("ImportUUIDBehavior");
                    if (str != null) {
                        try {
                            uuidBehavior = Integer.parseInt(str);
                        }
                        catch (NumberFormatException e) {
                            throw new DavException(400);
                        }
                    }
                    if (this.getTransactionId() == null) {
                        this.getRepositorySession().getWorkspace().importXML(itemPath, in, uuidBehavior);
                    } else {
                        this.getRepositorySession().importXML(itemPath, in, uuidBehavior);
                    }
                }
            } else {
                if (in == null) {
                    throw new DavException(400, "Cannot create a new non-collection resource without request body.");
                }
                String ct = inputContext.getContentType();
                int type = JcrValueType.typeFromContentType(ct);
                if (type != 0) {
                    String charSet;
                    int pos = ct.indexOf(59);
                    String string = charSet = pos > -1 ? ct.substring(pos) : "UTF-8";
                    if (type == 2) {
                        n.setProperty(memberName, inputContext.getInputStream());
                    } else {
                        String line;
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputContext.getInputStream(), charSet));
                        StringBuffer value = new StringBuffer();
                        while ((line = r.readLine()) != null) {
                            value.append(line);
                        }
                        n.setProperty(memberName, value.toString(), type);
                    }
                } else {
                    tmpFile = File.createTempFile(TMP_PREFIX + Text.escape(memberName), null, null);
                    FileOutputStream out = new FileOutputStream(tmpFile);
                    IOUtil.spool(in, out);
                    out.close();
                    ValuesProperty vp = this.buildValuesProperty(new FileInputStream(tmpFile));
                    if (vp != null) {
                        if (JCR_VALUE.equals(vp.getName())) {
                            n.setProperty(memberName, vp.getJcrValue());
                        } else {
                            n.setProperty(memberName, vp.getJcrValues());
                        }
                    } else {
                        n.setProperty(memberName, new FileInputStream(tmpFile));
                    }
                }
            }
            if (resource.exists() && resource instanceof AbstractItemResource) {
                ((AbstractItemResource)resource).complete();
            } else {
                this.complete();
            }
        }
        catch (ItemExistsException e) {
            throw new JcrDavException(e, 405);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
        catch (IOException e) {
            throw new DavException(422, e.getMessage());
        }
        finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }

    @Override
    public DavResourceIterator getMembers() {
        ArrayList<DavResource> memberList = new ArrayList<DavResource>();
        if (this.exists()) {
            try {
                Node n = (Node)this.item;
                NodeIterator it = n.getNodes();
                while (it.hasNext()) {
                    Node node = it.nextNode();
                    DavResourceLocator loc = this.getLocatorFromItem(node);
                    memberList.add(this.createResourceFromLocator(loc));
                }
                PropertyIterator propIt = n.getProperties();
                while (propIt.hasNext()) {
                    Property prop = propIt.nextProperty();
                    DavResourceLocator loc = this.getLocatorFromItem(prop);
                    memberList.add(this.createResourceFromLocator(loc));
                }
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }
            catch (DavException e) {
                log.error(e.getMessage());
            }
        }
        return new DavResourceIteratorImpl(memberList);
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        Session session = this.getRepositorySession();
        try {
            String itemPath = member.getLocator().getRepositoryPath();
            if (!this.exists() || !session.itemExists(itemPath)) {
                throw new DavException(404);
            }
            if (!this.getResourcePath().equals(Text.getRelativeParent(member.getResourcePath(), 1))) {
                throw new DavException(409, member.getResourcePath() + "is not member of this resource (" + this.getResourcePath() + ")");
            }
            this.getRepositorySession().getItem(itemPath).remove();
            this.complete();
        }
        catch (RepositoryException e) {
            log.error("Unexpected error: " + e.getMessage());
            throw new JcrDavException(e);
        }
    }

    @Override
    public boolean hasLock(Type type, Scope scope) {
        if (this.isLockable(type, scope)) {
            if (Type.WRITE.equals(type)) {
                try {
                    return ((Node)this.item).isLocked();
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
            } else {
                return super.hasLock(type, scope);
            }
        }
        return false;
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope) {
        ActiveLock lock = null;
        if (Type.WRITE.equals(type)) {
            try {
                if (!this.exists()) {
                    log.warn("Unable to retrieve lock: no item found at '" + this.getResourcePath() + "'");
                } else if (((Node)this.item).isLocked()) {
                    Lock jcrLock = ((Node)this.item).getLock();
                    lock = new JcrActiveLock(jcrLock);
                    DavResourceLocator locator = super.getLocator();
                    String lockroot = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), jcrLock.getNode().getPath(), false).getHref(false);
                    lock.setLockroot(lockroot);
                }
            }
            catch (AccessDeniedException e) {
                log.error("Error while accessing resource lock: " + e.getMessage());
            }
            catch (UnsupportedRepositoryOperationException e) {
                log.error("Error while accessing resource lock: " + e.getMessage());
            }
            catch (RepositoryException e) {
                log.error("Error while accessing resource lock: " + e.getMessage());
            }
        } else {
            lock = super.getLock(type, scope);
        }
        return lock;
    }

    @Override
    public ActiveLock lock(LockInfo reqLockInfo) throws DavException {
        if (!this.isLockable(reqLockInfo.getType(), reqLockInfo.getScope())) {
            throw new DavException(412);
        }
        if (Type.WRITE.equals(reqLockInfo.getType())) {
            if (!this.exists()) {
                log.warn("Cannot create a write lock for non-existing JCR node (" + this.getResourcePath() + ")");
                throw new DavException(404);
            }
            try {
                boolean sessionScoped = EXCLUSIVE_SESSION.equals(reqLockInfo.getScope());
                long timeout = reqLockInfo.getTimeout();
                timeout = timeout == Integer.MAX_VALUE ? Long.MAX_VALUE : (timeout /= 1000L);
                LockManager lockMgr = this.getRepositorySession().getWorkspace().getLockManager();
                Lock jcrLock = lockMgr.lock(this.item.getPath(), reqLockInfo.isDeep(), sessionScoped, timeout, reqLockInfo.getOwner());
                JcrActiveLock lock = new JcrActiveLock(jcrLock);
                this.getSession().addReference(lock.getToken());
                return lock;
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }
        return super.lock(reqLockInfo);
    }

    @Override
    public ActiveLock refreshLock(LockInfo reqLockInfo, String lockToken) throws DavException {
        if (lockToken == null) {
            throw new DavException(412);
        }
        ActiveLock lock = this.getLock(reqLockInfo.getType(), reqLockInfo.getScope());
        if (lock == null) {
            throw new DavException(412, "No lock with the given scope/type present on this resource.");
        }
        if (Type.WRITE.equals(lock.getType())) {
            try {
                Lock jcrLock = ((Node)this.item).getLock();
                jcrLock.refresh();
                return new JcrActiveLock(jcrLock);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }
        return super.refreshLock(reqLockInfo, lockToken);
    }

    @Override
    public void unlock(String lockToken) throws DavException {
        ActiveLock lock = this.getWriteLock();
        if (lock != null && lockToken.equals(lock.getToken())) {
            try {
                ((Node)this.item).unlock();
                this.getSession().removeReference(lock.getToken());
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            super.unlock(lockToken);
        }
    }

    private ActiveLock getWriteLock() throws DavException {
        if (!this.exists()) {
            throw new DavException(404, "Unable to retrieve write lock for non existing repository item (" + this.getResourcePath() + ")");
        }
        ActiveLock writeLock = this.getLock(Type.WRITE, Scope.EXCLUSIVE);
        if (writeLock == null) {
            writeLock = this.getLock(Type.WRITE, EXCLUSIVE_SESSION);
        }
        return writeLock;
    }

    @Override
    public boolean isOrderable() {
        boolean orderable = false;
        if (this.exists()) {
            try {
                orderable = ((Node)this.item).getPrimaryNodeType().hasOrderableChildNodes();
            }
            catch (RepositoryException e) {
                log.warn(e.getMessage());
            }
        }
        return orderable;
    }

    @Override
    public void orderMembers(OrderPatch orderPatch) throws DavException {
        if (!this.isOrderable()) {
            throw new DavException(405);
        }
        if (!"DAV:custom".equalsIgnoreCase(orderPatch.getOrderingType())) {
            throw new DavException(422, "Only DAV:custom ordering type supported.");
        }
        Node n = (Node)this.item;
        try {
            for (OrderPatch.Member instruction : orderPatch.getOrderInstructions()) {
                String srcRelPath = Text.unescape(instruction.getMemberHandle());
                Position pos = instruction.getPosition();
                String destRelPath = this.getRelDestinationPath(pos, n.getNodes());
                n.orderBefore(srcRelPath, destRelPath);
            }
            this.complete();
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    private String getRelDestinationPath(Position position, NodeIterator childNodes) throws RepositoryException {
        String destRelPath = null;
        if ("first".equals(position.getType())) {
            if (childNodes.hasNext()) {
                Node firstChild = childNodes.nextNode();
                destRelPath = Text.getName(firstChild.getPath());
            }
            if (destRelPath == null) {
                throw new ItemNotFoundException("No 'first' item found for reordering.");
            }
        } else if ("after".equals(position.getType())) {
            String afterRelPath = position.getSegment();
            boolean found = false;
            while (childNodes.hasNext() && destRelPath == null) {
                String childRelPath = Text.getName(childNodes.nextNode().getPath());
                if (found) {
                    destRelPath = childRelPath;
                    continue;
                }
                found = afterRelPath.equals(childRelPath);
            }
        } else {
            destRelPath = position.getSegment();
        }
        if (destRelPath != null) {
            destRelPath = Text.unescape(destRelPath);
        }
        return destRelPath;
    }

    @Override
    protected void initLockSupport() {
        super.initLockSupport();
        try {
            if (this.exists() && ((Node)this.item).isNodeType("mix:lockable")) {
                this.supportedLock.addEntry(Type.WRITE, Scope.EXCLUSIVE);
                this.supportedLock.addEntry(new SessionScopedLockEntry());
            }
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    protected void initSupportedReports() {
        super.initSupportedReports();
        if (this.exists()) {
            this.supportedReports.addReportType(ExportViewReport.EXPORTVIEW_REPORT);
            this.supportedReports.addReportType(LocateCorrespondingNodeReport.LOCATE_CORRESPONDING_NODE_REPORT);
        }
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (this.exists()) {
            this.names.addAll(JcrDavPropertyNameSet.NODE_SET);
            if (this.isReferenceable()) {
                this.names.add(JCR_UUID);
            }
            if (this.hasPrimaryItem()) {
                this.names.add(JCR_PRIMARYITEM);
            }
            if (this.isOrderable()) {
                this.names.add(OrderingConstants.ORDERING_TYPE);
            }
            if (SecurityUtils.supportsAccessControl(this.getRepositorySession())) {
                this.names.add(SecurityConstants.SUPPORTED_PRIVILEGE_SET);
                this.names.add(SecurityConstants.CURRENT_USER_PRIVILEGE_SET);
            }
        }
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        if (this.exists()) {
            this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETCONTENTTYPE, "text/xml"));
            Node n = (Node)this.item;
            try {
                this.properties.add(new NodeTypeProperty(JCR_PRIMARYNODETYPE, n.getPrimaryNodeType(), false));
                this.properties.add(new NodeTypeProperty(JCR_MIXINNODETYPES, n.getMixinNodeTypes(), false));
            }
            catch (RepositoryException e) {
                log.error("Failed to retrieve node-specific property: " + e);
            }
        }
    }

    @Override
    protected String getCreatorDisplayName() {
        try {
            if (this.exists() && ((Node)this.item).hasProperty("{http://www.jcp.org/jcr/1.0}createdBy")) {
                return ((Node)this.item).getProperty("{http://www.jcp.org/jcr/1.0}createdBy").getString();
            }
        }
        catch (RepositoryException e) {
            log.warn("Error while accessing jcr:createdBy property");
        }
        return super.getCreatorDisplayName();
    }

    @Override
    protected String getCreationDate() {
        try {
            if (this.exists() && ((Node)this.item).hasProperty("jcr:created")) {
                long creationTime = ((Node)this.item).getProperty("jcr:created").getValue().getLong();
                return HttpDateFormat.creationDateFormat().format(new Date(creationTime));
            }
        }
        catch (RepositoryException e) {
            log.warn("Error while accessing jcr:created property");
        }
        return super.getCreationDate();
    }

    protected HrefProperty getHrefProperty(DavPropertyName name, Item[] values, boolean isProtected) {
        String[] pHref = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            pHref[i] = this.getLocatorFromItem(values[i]).getHref(true);
        }
        return new HrefProperty(name, pHref, isProtected);
    }

    protected void addHrefProperty(DavPropertyName name, Item[] values, boolean isProtected) {
        this.properties.add(this.getHrefProperty(name, values, isProtected));
    }

    protected HrefProperty getHrefProperty(DavPropertyName name, PropertyIterator itemIterator, boolean isProtected) {
        ArrayList<Property> l = new ArrayList<Property>();
        while (itemIterator.hasNext()) {
            l.add(itemIterator.nextProperty());
        }
        return this.getHrefProperty(name, l.toArray(new Property[l.size()]), isProtected);
    }

    protected void addHrefProperty(DavPropertyName name, PropertyIterator itemIterator, boolean isProtected) {
        this.properties.add(this.getHrefProperty(name, itemIterator, isProtected));
    }

    protected HrefProperty getHrefProperty(DavPropertyName name, VersionIterator itemIterator, boolean isProtected) {
        ArrayList<Version> l = new ArrayList<Version>();
        while (itemIterator.hasNext()) {
            l.add(itemIterator.nextVersion());
        }
        return this.getHrefProperty(name, l.toArray(new Version[l.size()]), isProtected);
    }

    protected void addHrefProperty(DavPropertyName name, VersionIterator itemIterator, boolean isProtected) {
        this.properties.add(this.getHrefProperty(name, itemIterator, isProtected));
    }

    private ValuesProperty buildValuesProperty(InputStream in) {
        String errorMsg = "Cannot parse stream into a 'ValuesProperty'.";
        try {
            Document reqBody = DomUtil.parseDocument(in);
            DefaultDavProperty<?> defaultProp = DefaultDavProperty.createFromXml(reqBody.getDocumentElement());
            ValuesProperty vp = new ValuesProperty(defaultProp, 1, this.getRepositorySession().getValueFactory());
            return vp;
        }
        catch (IOException e) {
            log.debug(errorMsg, (Throwable)e);
        }
        catch (ParserConfigurationException e) {
            log.debug(errorMsg, (Throwable)e);
        }
        catch (SAXException e) {
            log.debug(errorMsg, (Throwable)e);
        }
        catch (DavException e) {
            log.debug(errorMsg, (Throwable)e);
        }
        catch (RepositoryException e) {
            log.debug(errorMsg, (Throwable)e);
        }
        return null;
    }

    private boolean hasPrimaryItem() {
        try {
            return this.exists() && ((Node)this.item).getPrimaryNodeType().getPrimaryItemName() != null;
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    private boolean isReferenceable() {
        try {
            return this.exists() && ((Node)this.item).isNodeType("mix:referenceable");
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }
}

