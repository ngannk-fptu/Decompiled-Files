/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.observation.EventJournal;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.jcr.AbstractItemResource;
import org.apache.jackrabbit.webdav.jcr.DefaultItemResource;
import org.apache.jackrabbit.webdav.jcr.EventJournalResourceImpl;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.RootCollection;
import org.apache.jackrabbit.webdav.jcr.VersionControlledItemCollection;
import org.apache.jackrabbit.webdav.jcr.WorkspaceResourceImpl;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.jcr.version.VersionHistoryItemCollection;
import org.apache.jackrabbit.webdav.jcr.version.VersionItemCollection;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;
import org.apache.jackrabbit.webdav.transaction.TransactionDavServletRequest;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.version.DeltaVServletRequest;
import org.apache.jackrabbit.webdav.version.VersionControlledResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DavResourceFactoryImpl
implements DavResourceFactory {
    private static Logger log = LoggerFactory.getLogger(DavResourceFactoryImpl.class);
    private final TxLockManagerImpl txMgr;
    private final SubscriptionManager subsMgr;

    public DavResourceFactoryImpl(TxLockManagerImpl txMgr, SubscriptionManager subsMgr) {
        this.txMgr = txMgr;
        this.subsMgr = subsMgr;
    }

    @Override
    public DavResource createResource(DavResourceLocator locator, DavServletRequest request, DavServletResponse response) throws DavException {
        DavResource resource;
        JcrDavSession.checkImplementation(request.getDavSession());
        JcrDavSession session = (JcrDavSession)request.getDavSession();
        String type = request.getParameter("type");
        if (locator.isRootLocation()) {
            resource = new RootCollection(locator, session, this);
        } else {
            if ("journal".equals(type) && locator.getResourcePath().equals(locator.getWorkspacePath())) {
                try {
                    EventJournal ej = session.getRepositorySession().getWorkspace().getObservationManager().getEventJournal();
                    if (ej == null) {
                        throw new DavException(501, "event journal not supported");
                    }
                    resource = new EventJournalResourceImpl(ej, locator, session, request, this);
                }
                catch (AccessDeniedException ex) {
                    throw new DavException(401, (Throwable)ex);
                }
                catch (RepositoryException ex) {
                    throw new DavException(400, (Throwable)ex);
                }
            }
            if (locator.getResourcePath().equals(locator.getWorkspacePath())) {
                resource = new WorkspaceResourceImpl(locator, session, this);
            } else {
                try {
                    String labelHeader;
                    boolean versionable;
                    resource = this.createResourceForItem(locator, session);
                    Item item = this.getItem(session, locator);
                    boolean bl = versionable = item.isNode() && ((Node)item).isNodeType("mix:versionable");
                    if (request instanceof DeltaVServletRequest && versionable && (labelHeader = ((DeltaVServletRequest)request).getLabel()) != null && DavMethods.isMethodAffectedByLabel(request) && this.isVersionControlled(resource)) {
                        Version v = ((Node)item).getVersionHistory().getVersionByLabel(labelHeader);
                        DavResourceLocator vloc = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), v.getPath(), false);
                        resource = new VersionItemCollection(vloc, session, this, v);
                    }
                }
                catch (PathNotFoundException e) {
                    resource = DavMethods.getMethodCode(request.getMethod()) == 9 ? new VersionControlledItemCollection(locator, session, this, null) : new DefaultItemResource(locator, session, this, null);
                }
                catch (RepositoryException e) {
                    log.error("Failed to build resource from item '" + locator.getRepositoryPath() + "'");
                    throw new JcrDavException(e);
                }
            }
        }
        if (request instanceof TransactionDavServletRequest && resource instanceof TransactionResource) {
            ((TransactionResource)resource).init(this.txMgr, ((TransactionDavServletRequest)request).getTransactionId());
        }
        if (resource instanceof ObservationResource) {
            ((ObservationResource)resource).init(this.subsMgr);
        }
        return resource;
    }

    @Override
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
        DavResource resource;
        JcrDavSession.checkImplementation(session);
        JcrDavSession sessionImpl = (JcrDavSession)session;
        if (locator.isRootLocation()) {
            resource = new RootCollection(locator, sessionImpl, this);
        } else if (locator.getResourcePath().equals(locator.getWorkspacePath())) {
            resource = new WorkspaceResourceImpl(locator, sessionImpl, this);
        } else {
            try {
                resource = this.createResourceForItem(locator, sessionImpl);
            }
            catch (RepositoryException e) {
                log.debug("Creating resource for non-existing repository item: " + locator.getRepositoryPath());
                resource = new VersionControlledItemCollection(locator, sessionImpl, this, null);
            }
        }
        resource.addLockManager(this.txMgr);
        if (resource instanceof ObservationResource) {
            ((ObservationResource)resource).init(this.subsMgr);
        }
        return resource;
    }

    private DavResource createResourceForItem(DavResourceLocator locator, JcrDavSession sessionImpl) throws RepositoryException, DavException {
        Item item = this.getItem(sessionImpl, locator);
        AbstractItemResource resource = item.isNode() ? (item instanceof Version ? new VersionItemCollection(locator, sessionImpl, this, item) : (item instanceof VersionHistory ? new VersionHistoryItemCollection(locator, sessionImpl, this, item) : new VersionControlledItemCollection(locator, sessionImpl, this, item))) : new DefaultItemResource(locator, sessionImpl, this, item);
        return resource;
    }

    protected Item getItem(JcrDavSession sessionImpl, DavResourceLocator locator) throws PathNotFoundException, RepositoryException {
        return sessionImpl.getRepositorySession().getItem(locator.getRepositoryPath());
    }

    private boolean isVersionControlled(DavResource resource) {
        boolean vc = false;
        if (resource instanceof VersionControlledResource) {
            try {
                vc = ((VersionControlledResource)resource).getVersionHistory() != null;
            }
            catch (DavException e) {
                log.debug("Resource '" + resource.getHref() + "' is not version-controlled.");
            }
        }
        return vc;
    }
}

