/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.server.io.AbstractExportContext;
import org.apache.jackrabbit.server.io.CopyMoveContextImpl;
import org.apache.jackrabbit.server.io.DefaultIOListener;
import org.apache.jackrabbit.server.io.DeleteContextImpl;
import org.apache.jackrabbit.server.io.DeleteManager;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.ExportContextImpl;
import org.apache.jackrabbit.server.io.IOListener;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.ImportContextImpl;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.bind.BindConstants;
import org.apache.jackrabbit.webdav.bind.BindableResource;
import org.apache.jackrabbit.webdav.bind.ParentElement;
import org.apache.jackrabbit.webdav.bind.ParentSet;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.lock.JcrActiveLock;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.simple.ItemFilter;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DavResourceImpl
implements DavResource,
BindableResource,
JcrConstants {
    private static final Logger log = LoggerFactory.getLogger(DavResourceImpl.class);
    public static final String METHODS = "OPTIONS, GET, HEAD, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK, BIND, REBIND, UNBIND";
    public static final String COMPLIANCE_CLASSES = DavCompliance.concatComplianceClasses(new String[]{"1", "2", "3", "bind"});
    private DavResourceFactory factory;
    private org.apache.jackrabbit.webdav.lock.LockManager lockManager;
    private JcrDavSession session;
    private Node node;
    private DavResourceLocator locator;
    protected DavPropertySet properties = new DavPropertySet();
    protected boolean propsInitialized = false;
    private boolean isCollection = true;
    private String rfc4122Uri;
    private ResourceConfig config;
    private long modificationTime = -1L;

    public DavResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, boolean isCollection) throws DavException {
        this(locator, factory, session, config, null);
        this.isCollection = isCollection;
    }

    public DavResourceImpl(DavResourceLocator locator, DavResourceFactory factory, DavSession session, ResourceConfig config, Node node) throws DavException {
        if (locator == null || session == null || config == null) {
            throw new IllegalArgumentException();
        }
        JcrDavSession.checkImplementation(session);
        this.session = (JcrDavSession)session;
        this.factory = factory;
        this.locator = locator;
        this.config = config;
        if (locator.getResourcePath() != null) {
            if (node != null) {
                this.node = node;
                this.isCollection = config.isCollectionResource(node);
                this.initRfc4122Uri();
            }
        } else {
            throw new DavException(404);
        }
    }

    private void initRfc4122Uri() {
        try {
            if (this.node.isNodeType("mix:referenceable")) {
                String uuid = this.node.getUUID();
                try {
                    UUID.fromString(uuid);
                    this.rfc4122Uri = "urn:uuid:" + uuid;
                }
                catch (IllegalArgumentException illegalArgumentException) {}
            }
        }
        catch (RepositoryException e) {
            log.warn("Error while detecting UUID", (Throwable)e);
        }
    }

    @Override
    public String getComplianceClass() {
        return COMPLIANCE_CLASSES;
    }

    @Override
    public String getSupportedMethods() {
        return METHODS;
    }

    @Override
    public boolean exists() {
        return this.node != null;
    }

    @Override
    public boolean isCollection() {
        return this.isCollection;
    }

    @Override
    public DavResourceLocator getLocator() {
        return this.locator;
    }

    @Override
    public String getResourcePath() {
        return this.locator.getResourcePath();
    }

    @Override
    public String getHref() {
        return this.locator.getHref(this.isCollection());
    }

    @Override
    public String getDisplayName() {
        String resPath = this.getResourcePath();
        return resPath != null ? Text.getName(resPath) : resPath;
    }

    @Override
    public long getModificationTime() {
        this.initProperties();
        return this.modificationTime;
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        if (this.exists() && outputContext != null) {
            ExportContext exportCtx = this.getExportContext(outputContext);
            if (!this.config.getIOManager().exportContent(exportCtx, this)) {
                throw new IOException("Unexpected Error while spooling resource.");
            }
        }
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        this.initProperties();
        return this.properties.get(name);
    }

    @Override
    public DavPropertySet getProperties() {
        this.initProperties();
        return this.properties;
    }

    @Override
    public DavPropertyName[] getPropertyNames() {
        return this.getProperties().getPropertyNames();
    }

    protected void initProperties() {
        Set<ParentElement> parentElements;
        if (!this.exists() || this.propsInitialized) {
            return;
        }
        try {
            this.config.getPropertyManager().exportProperties(this.getPropertyExportContext(), this.isCollection());
        }
        catch (RepositoryException e) {
            log.warn("Error while accessing resource properties", (Throwable)e);
        }
        if (this.getDisplayName() != null) {
            this.properties.add(new DefaultDavProperty<String>(DavPropertyName.DISPLAYNAME, this.getDisplayName()));
        }
        if (this.isCollection()) {
            this.properties.add(new ResourceType(1));
            this.properties.add(new DefaultDavProperty<String>(DavPropertyName.ISCOLLECTION, "1"));
        } else {
            this.properties.add(new ResourceType(0));
            this.properties.add(new DefaultDavProperty<String>(DavPropertyName.ISCOLLECTION, "0"));
        }
        if (this.rfc4122Uri != null) {
            this.properties.add(new HrefProperty(BindConstants.RESOURCEID, this.rfc4122Uri, true));
        }
        if (!(parentElements = this.getParentElements()).isEmpty()) {
            this.properties.add(new ParentSet(parentElements));
        }
        this.properties.add(new LockDiscovery(this.getLock(Type.WRITE, Scope.EXCLUSIVE)));
        SupportedLock supportedLock = new SupportedLock();
        supportedLock.addEntry(Type.WRITE, Scope.EXCLUSIVE);
        this.properties.add(supportedLock);
        this.propsInitialized = true;
    }

    @Override
    public void setProperty(DavProperty<?> property) throws DavException {
        this.alterProperty(property);
    }

    @Override
    public void removeProperty(DavPropertyName propertyName) throws DavException {
        this.alterProperty(propertyName);
    }

    private void alterProperty(PropEntry prop) throws DavException {
        if (this.isLocked(this)) {
            throw new DavException(423);
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        try {
            List<PropEntry> list = Collections.singletonList(prop);
            this.alterProperties(list);
            Map<PropEntry, ?> failure = this.config.getPropertyManager().alterProperties(this.getPropertyImportContext(list), this.isCollection());
            if (!failure.isEmpty()) {
                this.node.refresh(false);
                throw new DavException(500);
            }
            this.node.save();
        }
        catch (RepositoryException e) {
            JcrDavException je = new JcrDavException(e);
            try {
                this.node.refresh(false);
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
            throw je;
        }
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        if (this.isLocked(this)) {
            throw new DavException(423);
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        MultiStatusResponse msr = new MultiStatusResponse(this.getHref(), null);
        try {
            Map<PropEntry, ?> failures = this.config.getPropertyManager().alterProperties(this.getPropertyImportContext(changeList), this.isCollection());
            if (failures.isEmpty()) {
                this.node.save();
            } else {
                this.node.refresh(false);
            }
            for (PropEntry propEntry : changeList) {
                int statusCode;
                if (failures.containsKey(propEntry)) {
                    Object error = failures.get(propEntry);
                    statusCode = error instanceof RepositoryException ? new JcrDavException((RepositoryException)error).getErrorCode() : 500;
                } else {
                    int n = statusCode = failures.isEmpty() ? 200 : 424;
                }
                if (propEntry instanceof DavProperty) {
                    msr.add(((DavProperty)propEntry).getName(), statusCode);
                    continue;
                }
                msr.add((DavPropertyName)propEntry, statusCode);
            }
            return msr;
        }
        catch (RepositoryException e) {
            try {
                this.node.refresh(false);
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
            throw new JcrDavException(e);
        }
    }

    @Override
    public DavResource getCollection() {
        DavResource parent = null;
        if (this.getResourcePath() != null && !this.getResourcePath().equals("/")) {
            String parentPath = Text.getRelativeParent(this.getResourcePath(), 1);
            if (parentPath.equals("")) {
                parentPath = "/";
            }
            DavResourceLocator parentloc = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), parentPath);
            try {
                parent = this.factory.createResource(parentloc, this.session);
            }
            catch (DavException davException) {
                // empty catch block
            }
        }
        return parent;
    }

    @Override
    public DavResourceIterator getMembers() {
        ArrayList<DavResource> list = new ArrayList<DavResource>();
        if (this.exists() && this.isCollection()) {
            try {
                NodeIterator it = this.node.getNodes();
                while (it.hasNext()) {
                    Node n = it.nextNode();
                    if (!this.isFilteredItem(n)) {
                        DavResourceLocator resourceLocator = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), n.getPath(), false);
                        DavResource childRes = this.factory.createResource(resourceLocator, this.session);
                        list.add(childRes);
                        continue;
                    }
                    log.debug("Filtered resource '" + n.getName() + "'.");
                }
            }
            catch (RepositoryException repositoryException) {
            }
            catch (DavException davException) {
                // empty catch block
            }
        }
        return new DavResourceIteratorImpl(list);
    }

    @Override
    public void addMember(DavResource member, InputContext inputContext) throws DavException {
        if (!this.exists()) {
            throw new DavException(409);
        }
        if (this.isLocked(this) || this.isLocked(member)) {
            throw new DavException(423);
        }
        try {
            if (this.isFilteredResource(member) || this.node.getDefinition().isProtected()) {
                log.debug("Forbidden to add member: " + member.getDisplayName());
                throw new DavException(403);
            }
            String memberName = Text.getName(member.getLocator().getRepositoryPath());
            ImportContext ctx = this.getImportContext(inputContext, memberName);
            if (!this.config.getIOManager().importContent(ctx, member)) {
                throw new DavException(415);
            }
            this.node.save();
        }
        catch (RepositoryException e) {
            log.error("Error while importing resource: " + e.toString());
            throw new JcrDavException(e);
        }
        catch (IOException e) {
            log.error("Error while importing resource: " + e.toString());
            throw new DavException(500, e.getMessage());
        }
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        if (!this.exists() || !member.exists()) {
            throw new DavException(404);
        }
        if (this.isLocked(this) || this.isLocked(member)) {
            throw new DavException(423);
        }
        if (this.isFilteredResource(member)) {
            log.debug("Avoid removal of filtered resource: " + member.getDisplayName());
            throw new DavException(403);
        }
        DeleteManager dm = this.config.getDeleteManager();
        dm.delete(new DeleteContextImpl(this.getJcrSession()), member);
        try {
            ActiveLock lock;
            if (!this.isJcrLockable() && (lock = this.getLock(Type.WRITE, Scope.EXCLUSIVE)) != null) {
                this.lockManager.releaseLock(lock.getToken(), member);
            }
        }
        catch (DavException davException) {
            // empty catch block
        }
    }

    @Override
    public void move(DavResource destination) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (this.isLocked(this)) {
            throw new DavException(423);
        }
        if (this.isFilteredResource(destination)) {
            throw new DavException(403);
        }
        this.checkSameWorkspace(destination.getLocator());
        if (!this.config.getCopyMoveManager().move(new CopyMoveContextImpl(this.getJcrSession()), this, destination)) {
            throw new DavException(415);
        }
    }

    @Override
    public void copy(DavResource destination, boolean shallow) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (this.isLocked(destination)) {
            throw new DavException(423);
        }
        if (this.isFilteredResource(destination)) {
            throw new DavException(403);
        }
        this.checkSameWorkspace(destination.getLocator());
        if (!this.config.getCopyMoveManager().copy(new CopyMoveContextImpl(this.getJcrSession(), shallow), this, destination)) {
            throw new DavException(415);
        }
    }

    @Override
    public boolean isLockable(Type type, Scope scope) {
        return Type.WRITE.equals(type) && Scope.EXCLUSIVE.equals(scope);
    }

    @Override
    public boolean hasLock(Type type, Scope scope) {
        return this.getLock(type, scope) != null;
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope) {
        ActiveLock lock = null;
        if (this.exists() && Type.WRITE.equals(type) && Scope.EXCLUSIVE.equals(scope)) {
            try {
                Lock jcrLock;
                if (this.node.isLocked() && (jcrLock = this.node.getLock()) != null && jcrLock.isLive()) {
                    lock = new JcrActiveLock(jcrLock);
                    String lockroot = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), jcrLock.getNode().getPath(), false).getHref(false);
                    lock.setLockroot(lockroot);
                }
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
            if (lock == null) {
                lock = this.lockManager.getLock(type, scope, this);
            }
        }
        return lock;
    }

    @Override
    public ActiveLock[] getLocks() {
        ActiveLock[] activeLockArray;
        ActiveLock writeLock = this.getLock(Type.WRITE, Scope.EXCLUSIVE);
        if (writeLock != null) {
            ActiveLock[] activeLockArray2 = new ActiveLock[1];
            activeLockArray = activeLockArray2;
            activeLockArray2[0] = writeLock;
        } else {
            activeLockArray = new ActiveLock[]{};
        }
        return activeLockArray;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ActiveLock lock(LockInfo lockInfo) throws DavException {
        ActiveLock lock = null;
        if (!this.isLockable(lockInfo.getType(), lockInfo.getScope())) throw new DavException(412, "Unsupported lock type or scope.");
        if (!this.isJcrLockable()) return this.lockManager.createLock(lockInfo, this);
        try {
            LockManager lockMgr = this.node.getSession().getWorkspace().getLockManager();
            long timeout = lockInfo.getTimeout();
            timeout = timeout == Integer.MAX_VALUE ? Long.MAX_VALUE : (timeout /= 1000L);
            Lock jcrLock = lockMgr.lock(this.node.getPath(), lockInfo.isDeep(), false, timeout, lockInfo.getOwner());
            if (jcrLock == null) return lock;
            return new JcrActiveLock(jcrLock);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public ActiveLock refreshLock(LockInfo lockInfo, String lockToken) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        ActiveLock lock = this.getLock(lockInfo.getType(), lockInfo.getScope());
        if (lock == null) {
            throw new DavException(412, "No lock with the given type/scope present on resource " + this.getResourcePath());
        }
        if (lock instanceof JcrActiveLock) {
            try {
                this.node.getLock().refresh();
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        } else {
            lock = this.lockManager.refreshLock(lockInfo, lockToken, this);
        }
        return lock;
    }

    @Override
    public void unlock(String lockToken) throws DavException {
        ActiveLock lock = this.getLock(Type.WRITE, Scope.EXCLUSIVE);
        if (lock == null) {
            throw new DavException(412);
        }
        if (lock.isLockedByToken(lockToken)) {
            if (lock instanceof JcrActiveLock) {
                try {
                    this.node.unlock();
                }
                catch (RepositoryException e) {
                    throw new JcrDavException(e);
                }
            } else {
                this.lockManager.releaseLock(lockToken, this);
            }
        } else {
            throw new DavException(423);
        }
    }

    @Override
    public void addLockManager(org.apache.jackrabbit.webdav.lock.LockManager lockMgr) {
        this.lockManager = lockMgr;
    }

    @Override
    public DavResourceFactory getFactory() {
        return this.factory;
    }

    @Override
    public DavSession getSession() {
        return this.session;
    }

    @Override
    public void bind(DavResource collection, DavResource newBinding) throws DavException {
        if (!this.exists()) {
            throw new DavException(412);
        }
        if (this.isLocked(collection)) {
            throw new DavException(423);
        }
        if (this.isFilteredResource(newBinding)) {
            throw new DavException(403);
        }
        this.checkSameWorkspace(collection.getLocator());
        try {
            if (!this.node.isNodeType("mix:shareable")) {
                if (!this.node.canAddMixin("mix:shareable")) {
                    throw new DavException(412);
                }
                this.node.addMixin("mix:shareable");
                this.node.save();
            }
            Workspace workspace = this.session.getRepositorySession().getWorkspace();
            workspace.clone(workspace.getName(), this.node.getPath(), newBinding.getLocator().getRepositoryPath(), false);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public void rebind(DavResource collection, DavResource newBinding) throws DavException {
        if (!this.exists()) {
            throw new DavException(412);
        }
        if (this.isLocked(this)) {
            throw new DavException(412);
        }
        if (this.isLocked(collection)) {
            throw new DavException(423);
        }
        if (this.isFilteredResource(newBinding)) {
            throw new DavException(403);
        }
        this.checkSameWorkspace(collection.getLocator());
        try {
            if (!this.node.isNodeType("mix:referenceable")) {
                throw new DavException(this.node.canAddMixin("mix:referenceable") ? 409 : 405);
            }
            this.getJcrSession().getWorkspace().move(this.locator.getRepositoryPath(), newBinding.getLocator().getRepositoryPath());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public Set<ParentElement> getParentElements() {
        try {
            if (this.node.getDepth() > 0) {
                HashSet<ParentElement> ps = new HashSet<ParentElement>();
                NodeIterator sharedSetIterator = this.node.getSharedSet();
                while (sharedSetIterator.hasNext()) {
                    Node sharednode = sharedSetIterator.nextNode();
                    DavResourceLocator loc = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), sharednode.getParent().getPath(), false);
                    ps.add(new ParentElement(loc.getHref(true), sharednode.getName()));
                }
                return ps;
            }
        }
        catch (UnsupportedRepositoryOperationException e) {
            log.debug("unable to calculate parent set", (Throwable)e);
        }
        catch (RepositoryException e) {
            log.warn("unable to calculate parent set", (Throwable)e);
        }
        return Collections.emptySet();
    }

    protected Node getNode() {
        return this.node;
    }

    protected ImportContext getImportContext(InputContext inputCtx, String systemId) throws IOException {
        return new ImportContextImpl(this.node, systemId, inputCtx, inputCtx != null ? inputCtx.getInputStream() : null, new DefaultIOListener(log), this.config.getDetector());
    }

    protected ExportContext getExportContext(OutputContext outputCtx) throws IOException {
        return new ExportContextImpl(this.node, outputCtx);
    }

    protected PropertyImportContext getPropertyImportContext(List<? extends PropEntry> changeList) {
        return new PropertyImportCtx(changeList);
    }

    protected PropertyExportContext getPropertyExportContext() {
        return new PropertyExportCtx();
    }

    private boolean isJcrLockable() {
        boolean lockable = false;
        if (this.exists()) {
            try {
                lockable = this.node.isNodeType("mix:lockable");
                if (!lockable && this.node.canAddMixin("mix:lockable")) {
                    this.node.addMixin("mix:lockable");
                    this.node.save();
                    lockable = true;
                }
            }
            catch (RepositoryException repositoryException) {
                // empty catch block
            }
        }
        return lockable;
    }

    private boolean isLocked(DavResource res) {
        ActiveLock lock = res.getLock(Type.WRITE, Scope.EXCLUSIVE);
        if (lock == null) {
            return false;
        }
        for (String sLockToken : this.session.getLockTokens()) {
            if (!sLockToken.equals(lock.getToken())) continue;
            return false;
        }
        return true;
    }

    private Session getJcrSession() {
        return this.session.getRepositorySession();
    }

    private boolean isFilteredResource(DavResource resource) {
        ItemFilter filter = this.config.getItemFilter();
        return filter != null && filter.isFilteredItem(resource.getDisplayName(), this.getJcrSession());
    }

    private boolean isFilteredItem(Item item) {
        ItemFilter filter = this.config.getItemFilter();
        return filter != null && filter.isFilteredItem(item);
    }

    private void checkSameWorkspace(DavResourceLocator otherLoc) throws DavException {
        String wspname = this.getJcrSession().getWorkspace().getName();
        if (!wspname.equals(otherLoc.getWorkspaceName())) {
            throw new DavException(403, "Workspace mismatch: expected '" + wspname + "'; found: '" + otherLoc.getWorkspaceName() + "'");
        }
    }

    private class PropertyImportCtx
    implements PropertyImportContext {
        private final IOListener ioListener = new DefaultIOListener(DavResourceImpl.access$400());
        private final List<? extends PropEntry> changeList;
        private boolean completed;

        private PropertyImportCtx(List<? extends PropEntry> changeList) {
            this.changeList = changeList;
        }

        @Override
        public Item getImportRoot() {
            return DavResourceImpl.this.node;
        }

        @Override
        public List<? extends PropEntry> getChangeList() {
            return Collections.unmodifiableList(this.changeList);
        }

        @Override
        public IOListener getIOListener() {
            return this.ioListener;
        }

        @Override
        public boolean hasStream() {
            return false;
        }

        @Override
        public void informCompleted(boolean success) {
            this.checkCompleted();
            this.completed = true;
        }

        @Override
        public boolean isCompleted() {
            return this.completed;
        }

        private void checkCompleted() {
            if (this.completed) {
                throw new IllegalStateException("PropertyImportContext has already been consumed.");
            }
        }
    }

    private class PropertyExportCtx
    extends AbstractExportContext
    implements PropertyExportContext {
        private PropertyExportCtx() {
            super(DavResourceImpl.this.node, false, null);
            this.setCreationTime(-1L);
            this.setModificationTime(-1L);
        }

        @Override
        public OutputStream getOutputStream() {
            return null;
        }

        @Override
        public void setContentLanguage(String contentLanguage) {
            if (contentLanguage != null) {
                DavResourceImpl.this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETCONTENTLANGUAGE, contentLanguage));
            }
        }

        @Override
        public void setContentLength(long contentLength) {
            if (contentLength > -1L) {
                DavResourceImpl.this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETCONTENTLENGTH, contentLength + ""));
            }
        }

        @Override
        public void setContentType(String mimeType, String encoding) {
            String contentType = IOUtil.buildContentType(mimeType, encoding);
            if (contentType != null) {
                DavResourceImpl.this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETCONTENTTYPE, contentType));
            }
        }

        @Override
        public void setCreationTime(long creationTime) {
            String created = IOUtil.getCreated(creationTime);
            DavResourceImpl.this.properties.add(new DefaultDavProperty<String>(DavPropertyName.CREATIONDATE, created));
        }

        @Override
        public void setModificationTime(long modTime) {
            if (modTime <= -1L) {
                DavResourceImpl.this.modificationTime = new Date().getTime();
            } else {
                DavResourceImpl.this.modificationTime = modTime;
            }
            String lastModified = IOUtil.getLastModified(DavResourceImpl.this.modificationTime);
            DavResourceImpl.this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETLASTMODIFIED, lastModified));
        }

        @Override
        public void setETag(String etag) {
            if (etag != null) {
                DavResourceImpl.this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETETAG, etag));
            }
        }

        @Override
        public void setProperty(Object propertyName, Object propertyValue) {
            if (propertyValue == null) {
                log.warn("Ignore 'setProperty' for " + propertyName + "with null value.");
                return;
            }
            if (propertyValue instanceof DavProperty) {
                DavResourceImpl.this.properties.add((DavProperty)propertyValue);
            } else {
                DavPropertyName pName = propertyName instanceof DavPropertyName ? (DavPropertyName)propertyName : DavPropertyName.create(propertyName.toString());
                DavResourceImpl.this.properties.add(new DefaultDavProperty<Object>(pName, propertyValue));
            }
        }
    }
}

