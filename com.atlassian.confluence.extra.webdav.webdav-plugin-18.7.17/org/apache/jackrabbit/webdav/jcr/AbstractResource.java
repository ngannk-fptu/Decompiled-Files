/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.jcr.Item;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.webdav.DavCompliance;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestContext;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.jcr.search.SearchResourceImpl;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.jcr.version.report.LocateByUuidReport;
import org.apache.jackrabbit.webdav.jcr.version.report.NodeTypesReport;
import org.apache.jackrabbit.webdav.jcr.version.report.RegisteredNamespacesReport;
import org.apache.jackrabbit.webdav.jcr.version.report.RepositoryDescriptorsReport;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.HrefProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.apache.jackrabbit.webdav.search.QueryGrammerSet;
import org.apache.jackrabbit.webdav.search.SearchInfo;
import org.apache.jackrabbit.webdav.search.SearchResource;
import org.apache.jackrabbit.webdav.server.WebdavRequestContextHolder;
import org.apache.jackrabbit.webdav.transaction.TransactionConstants;
import org.apache.jackrabbit.webdav.transaction.TransactionInfo;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.transaction.TxLockManager;
import org.apache.jackrabbit.webdav.util.HttpDateFormat;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.version.DeltaVResource;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.OptionsResponse;
import org.apache.jackrabbit.webdav.version.SupportedMethodSetProperty;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.version.report.SupportedReportSetProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

abstract class AbstractResource
implements DavResource,
TransactionResource,
DeltaVResource,
SearchResource {
    private static Logger log = LoggerFactory.getLogger(AbstractResource.class);
    private static final String COMPLIANCE_CLASSES = DavCompliance.concatComplianceClasses(new String[]{"1", "2", "3", "version-control", "version-history", "checkout-in-place", "label", "merge", "update", "workspace"});
    private final DavResourceLocator locator;
    private final JcrDavSession session;
    private final DavResourceFactory factory;
    private TxLockManagerImpl txMgr;
    private String transactionId;
    protected boolean initedProps;
    protected DavPropertySet properties = new DavPropertySet();
    protected DavPropertyNameSet names;
    protected SupportedLock supportedLock = new SupportedLock();
    protected SupportedReportSetProperty supportedReports = new SupportedReportSetProperty();

    AbstractResource(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory) {
        if (session == null) {
            throw new IllegalArgumentException("Creating AbstractItemResource: DavSession must not be null and must provide a JCR session.");
        }
        this.locator = locator;
        this.session = session;
        this.factory = factory;
    }

    @Override
    public String getComplianceClass() {
        return COMPLIANCE_CLASSES;
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
    public DavPropertyName[] getPropertyNames() {
        this.initPropertyNames();
        return this.names.getContent().toArray(new DavPropertyName[this.names.getContentSize()]);
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        AbstractDavProperty prop = this.getProperties().get(name);
        if (prop == null) {
            String workspaceHref;
            if (DeltaVConstants.SUPPORTED_METHOD_SET.equals(name)) {
                prop = new SupportedMethodSetProperty(this.getSupportedMethods().split(",\\s"));
            } else if (DeltaVConstants.SUPPORTED_REPORT_SET.equals(name)) {
                prop = this.supportedReports;
            } else if (DeltaVConstants.CREATOR_DISPLAYNAME.equals(name)) {
                prop = new DefaultDavProperty<String>(DeltaVConstants.CREATOR_DISPLAYNAME, this.getCreatorDisplayName(), true);
            } else if (DeltaVConstants.COMMENT.equals(name)) {
                prop = new DefaultDavProperty<Object>(DeltaVConstants.COMMENT, null, true);
            } else if (DeltaVConstants.WORKSPACE.equals(name) && (workspaceHref = this.getWorkspaceHref()) != null) {
                prop = new HrefProperty(DeltaVConstants.WORKSPACE, workspaceHref, true);
            }
        }
        return prop;
    }

    @Override
    public DavPropertySet getProperties() {
        if (!this.initedProps) {
            this.initProperties();
        }
        return this.properties;
    }

    @Override
    public void setProperty(DavProperty<?> property) throws DavException {
        throw new DavException(405);
    }

    @Override
    public void removeProperty(DavPropertyName propertyName) throws DavException {
        throw new DavException(405);
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        throw new DavException(405);
    }

    @Override
    public void move(DavResource destination) throws DavException {
        throw new DavException(405);
    }

    @Override
    public void copy(DavResource destination, boolean shallow) throws DavException {
        throw new DavException(405);
    }

    @Override
    public boolean isLockable(Type type, Scope scope) {
        return this.supportedLock.isSupportedLock(type, scope);
    }

    @Override
    public boolean hasLock(Type type, Scope scope) {
        return this.getLock(type, scope) != null;
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope) {
        ActiveLock lock = null;
        if (TransactionConstants.TRANSACTION.equals(type)) {
            lock = this.txMgr.getLock(type, scope, this);
        }
        return lock;
    }

    @Override
    public ActiveLock[] getLocks() {
        ArrayList<ActiveLock> locks = new ArrayList<ActiveLock>();
        ActiveLock l = this.getLock(TransactionConstants.TRANSACTION, TransactionConstants.LOCAL);
        if (l != null) {
            locks.add(l);
        }
        if ((l = this.getLock(TransactionConstants.TRANSACTION, TransactionConstants.GLOBAL)) != null) {
            locks.add(l);
        }
        if ((l = this.getLock(Type.WRITE, Scope.EXCLUSIVE)) != null) {
            locks.add(l);
        } else {
            l = this.getLock(Type.WRITE, ItemResourceConstants.EXCLUSIVE_SESSION);
            if (l != null) {
                locks.add(l);
            }
        }
        return locks.toArray(new ActiveLock[locks.size()]);
    }

    @Override
    public ActiveLock lock(LockInfo reqLockInfo) throws DavException {
        if (this.isLockable(reqLockInfo.getType(), reqLockInfo.getScope())) {
            return this.txMgr.createLock(reqLockInfo, (DavResource)this);
        }
        throw new DavException(412);
    }

    @Override
    public ActiveLock refreshLock(LockInfo info, String lockToken) throws DavException {
        return this.txMgr.refreshLock(info, lockToken, (DavResource)this);
    }

    @Override
    public void unlock(String lockToken) throws DavException {
        throw new DavException(412);
    }

    @Override
    public void addLockManager(LockManager lockMgr) {
        if (lockMgr instanceof TxLockManagerImpl) {
            this.txMgr = (TxLockManagerImpl)lockMgr;
        }
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
    public void init(TxLockManager txMgr, String transactionId) {
        this.txMgr = (TxLockManagerImpl)txMgr;
        this.transactionId = transactionId;
    }

    @Override
    public void unlock(String lockToken, TransactionInfo tInfo) throws DavException {
        this.txMgr.releaseLock(tInfo, lockToken, this);
    }

    @Override
    public String getTransactionId() {
        return this.transactionId;
    }

    @Override
    public OptionsResponse getOptionResponse(OptionsInfo optionsInfo) {
        OptionsResponse oR = null;
        if (optionsInfo != null) {
            oR = new OptionsResponse();
            if (optionsInfo.containsElement("version-history-collection-set", DeltaVConstants.NAMESPACE)) {
                String[] hrefs = new String[]{this.getLocatorFromItemPath("/jcr:system/jcr:versionStorage").getHref(true)};
                oR.addEntry("version-history-collection-set", DeltaVConstants.NAMESPACE, hrefs);
            }
            if (optionsInfo.containsElement("workspace-collection-set", DeltaVConstants.NAMESPACE)) {
                oR.addEntry("workspace-collection-set", DeltaVConstants.NAMESPACE, new String[0]);
            }
        }
        return oR;
    }

    @Override
    public Report getReport(ReportInfo reportInfo) throws DavException {
        if (reportInfo == null) {
            throw new DavException(400, "A REPORT request must provide a valid XML request body.");
        }
        if (!this.exists()) {
            throw new DavException(404);
        }
        if (!this.supportedReports.isSupportedReport(reportInfo)) {
            Element condition = null;
            try {
                condition = DomUtil.createDocument().createElementNS("DAV:", "supported-report");
            }
            catch (ParserConfigurationException parserConfigurationException) {
                // empty catch block
            }
            throw new DavException(409, "Unknown report '" + reportInfo.getReportName() + "' requested.", null, condition);
        }
        return ReportType.getType(reportInfo).createReport(this, reportInfo);
    }

    @Override
    public void addWorkspace(DavResource workspace) throws DavException {
        throw new DavException(403);
    }

    @Override
    public DavResource[] getReferenceResources(DavPropertyName hrefPropertyName) throws DavException {
        DavProperty<?> prop = this.getProperty(hrefPropertyName);
        if (prop == null || !(prop instanceof HrefProperty)) {
            throw new DavException(409, "Unknown Href-Property '" + hrefPropertyName + "' on resource " + this.getResourcePath());
        }
        List<String> hrefs = ((HrefProperty)prop).getHrefs();
        DavResource[] refResources = new DavResource[hrefs.size()];
        Iterator<String> hrefIter = hrefs.iterator();
        int i = 0;
        while (hrefIter.hasNext()) {
            refResources[i] = this.getResourceFromHref(hrefIter.next());
            ++i;
        }
        return refResources;
    }

    private DavResource getResourceFromHref(String href) throws DavException {
        DavResourceLocator locator = this.getLocator();
        String prefix = locator.getPrefix();
        DavResourceLocator loc = locator.getFactory().createResourceLocator(prefix, href);
        try {
            if (!this.getRepositorySession().itemExists(loc.getRepositoryPath())) {
                throw new DavException(404);
            }
            DavResource res = this.createResourceFromLocator(loc);
            return res;
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public QueryGrammerSet getQueryGrammerSet() {
        return new SearchResourceImpl(this.getLocator(), this.session).getQueryGrammerSet();
    }

    @Override
    public MultiStatus search(SearchInfo sInfo) throws DavException {
        return new SearchResourceImpl(this.getLocator(), this.session).search(sInfo);
    }

    protected void initPropertyNames() {
        this.names = new DavPropertyNameSet(JcrDavPropertyNameSet.BASE_SET);
    }

    protected void initProperties() {
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
        String lastModified = IOUtil.getLastModified(this.getModificationTime());
        this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETLASTMODIFIED, lastModified));
        this.properties.add(new DefaultDavProperty<String>(DavPropertyName.CREATIONDATE, this.getCreationDate()));
        this.properties.add(this.supportedLock);
        this.properties.add(new LockDiscovery(this.getLocks()));
        this.properties.add(new DefaultDavProperty<String>(ItemResourceConstants.JCR_WORKSPACE_NAME, this.getRepositorySession().getWorkspace().getName()));
    }

    protected DavResource createResourceFromLocator(DavResourceLocator loc) throws DavException {
        DavResource res = this.factory.createResource(loc, this.session);
        if (res instanceof AbstractResource) {
            ((AbstractResource)res).transactionId = this.transactionId;
        }
        return res;
    }

    protected DavResourceLocator getLocatorFromItemPath(String itemPath) {
        DavResourceLocator loc = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), itemPath, false);
        return loc;
    }

    protected DavResourceLocator getLocatorFromItem(Item repositoryItem) {
        String itemPath = null;
        try {
            if (repositoryItem != null) {
                itemPath = repositoryItem.getPath();
            }
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
        }
        return this.getLocatorFromItemPath(itemPath);
    }

    protected Session getRepositorySession() {
        return this.session.getRepositorySession();
    }

    protected abstract void initLockSupport();

    protected void initSupportedReports() {
        if (this.exists()) {
            this.supportedReports = new SupportedReportSetProperty(new ReportType[]{ReportType.EXPAND_PROPERTY, NodeTypesReport.NODETYPES_REPORT, LocateByUuidReport.LOCATE_BY_UUID_REPORT, RegisteredNamespacesReport.REGISTERED_NAMESPACES_REPORT, RepositoryDescriptorsReport.REPOSITORY_DESCRIPTORS_REPORT});
        }
    }

    protected abstract String getWorkspaceHref();

    protected String getCreatorDisplayName() {
        return null;
    }

    protected String getCreationDate() {
        return HttpDateFormat.creationDateFormat().format(new Date(0L));
    }

    protected String normalizeResourceHref(String href) {
        WebdavRequest request;
        if (href == null) {
            return href;
        }
        WebdavRequestContext requestContext = WebdavRequestContextHolder.getContext();
        WebdavRequest webdavRequest = request = requestContext != null ? requestContext.getRequest() : null;
        if (request == null) {
            log.error("WebdavRequest is unavailable in the current execution context.");
            return href;
        }
        String contextPath = request.getContextPath();
        if (!contextPath.isEmpty() && href.startsWith(contextPath)) {
            return href.substring(contextPath.length());
        }
        return href;
    }

    void registerEventListener(EventListener listener, String nodePath) throws RepositoryException {
        this.getRepositorySession().getWorkspace().getObservationManager().addEventListener(listener, 127, nodePath, true, null, null, false);
    }

    void unregisterEventListener(EventListener listener) throws RepositoryException {
        this.getRepositorySession().getWorkspace().getObservationManager().removeEventListener(listener);
    }

    class EListener
    implements EventListener {
        private static final int ALL_EVENTS = 127;
        private final DavPropertyNameSet propNameSet;
        private MultiStatus ms;

        EListener(DavPropertyNameSet propNameSet, MultiStatus ms) {
            this.propNameSet = propNameSet;
            this.ms = ms;
        }

        @Override
        public void onEvent(EventIterator events) {
            while (events.hasNext()) {
                try {
                    Event e = events.nextEvent();
                    DavResourceLocator loc = AbstractResource.this.getLocatorFromItemPath(e.getPath());
                    DavResource res = AbstractResource.this.createResourceFromLocator(loc);
                    this.ms.addResponse(new MultiStatusResponse(res, this.propNameSet));
                }
                catch (DavException e) {
                    log.error("Error while building MultiStatusResponse from Event: " + e.getMessage());
                }
                catch (RepositoryException e) {
                    log.error("Error while building MultiStatusResponse from Event: " + e.getMessage());
                }
            }
        }
    }
}

