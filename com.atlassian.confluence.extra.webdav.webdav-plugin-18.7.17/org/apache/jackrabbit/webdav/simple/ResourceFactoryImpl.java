/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.simple.DavResourceImpl;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.simple.VersionControlledResourceImpl;
import org.apache.jackrabbit.webdav.simple.VersionHistoryResourceImpl;
import org.apache.jackrabbit.webdav.simple.VersionResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFactoryImpl
implements DavResourceFactory {
    private static Logger log = LoggerFactory.getLogger(ResourceFactoryImpl.class);
    private final LockManager lockMgr;
    private final ResourceConfig resourceConfig;

    public ResourceFactoryImpl(LockManager lockMgr, ResourceConfig resourceConfig) {
        this.lockMgr = lockMgr;
        this.resourceConfig = resourceConfig;
    }

    @Override
    public DavResource createResource(DavResourceLocator locator, DavServletRequest request, DavServletResponse response) throws DavException {
        try {
            DavResource resource;
            Node node = this.getNode(request.getDavSession(), locator);
            if (node == null) {
                log.debug("Creating resource for non-existing repository node.");
                boolean isCollection = DavMethods.isCreateCollectionRequest(request);
                resource = this.createNullResource(locator, request.getDavSession(), isCollection);
            } else {
                resource = this.createResource(node, locator, request.getDavSession());
            }
            resource.addLockManager(this.lockMgr);
            return resource;
        }
        catch (RepositoryException e) {
            throw new DavException(500, (Throwable)e);
        }
    }

    @Override
    public DavResource createResource(DavResourceLocator locator, DavSession session) throws DavException {
        try {
            Node node = this.getNode(session, locator);
            DavResource resource = this.createResource(node, locator, session);
            resource.addLockManager(this.lockMgr);
            return resource;
        }
        catch (RepositoryException e) {
            throw new DavException(500, (Throwable)e);
        }
    }

    private Node getNode(DavSession sessionImpl, DavResourceLocator locator) throws RepositoryException {
        Node node = null;
        try {
            Session session;
            Item item;
            String repoPath = locator.getRepositoryPath();
            if (repoPath != null && (item = (session = ((JcrDavSession)sessionImpl).getRepositorySession()).getItem(repoPath)) instanceof Node) {
                node = (Node)item;
            }
        }
        catch (PathNotFoundException pathNotFoundException) {
            // empty catch block
        }
        return node;
    }

    private DavResource createNullResource(DavResourceLocator locator, DavSession session, boolean isCollection) throws DavException {
        JcrDavSession.checkImplementation(session);
        JcrDavSession sessionImpl = (JcrDavSession)session;
        DavResourceImpl resource = ResourceFactoryImpl.versioningSupported(sessionImpl.getRepositorySession()) ? new VersionControlledResourceImpl(locator, (DavResourceFactory)this, (DavSession)sessionImpl, this.resourceConfig, isCollection) : new DavResourceImpl(locator, (DavResourceFactory)this, (DavSession)sessionImpl, this.resourceConfig, isCollection);
        return resource;
    }

    private DavResource createResource(Node node, DavResourceLocator locator, DavSession session) throws DavException {
        JcrDavSession.checkImplementation(session);
        JcrDavSession sessionImpl = (JcrDavSession)session;
        DavResourceImpl resource = ResourceFactoryImpl.versioningSupported(sessionImpl.getRepositorySession()) ? (node instanceof Version ? new VersionResourceImpl(locator, (DavResourceFactory)this, (DavSession)sessionImpl, this.resourceConfig, (Item)node) : (node instanceof VersionHistory ? new VersionHistoryResourceImpl(locator, (DavResourceFactory)this, (DavSession)sessionImpl, this.resourceConfig, (Item)node) : new VersionControlledResourceImpl(locator, (DavResourceFactory)this, (DavSession)sessionImpl, this.resourceConfig, (Item)node))) : new DavResourceImpl(locator, (DavResourceFactory)this, session, this.resourceConfig, node);
        return resource;
    }

    private static boolean versioningSupported(Session repoSession) {
        String desc = repoSession.getRepository().getDescriptor("option.versioning.supported");
        return Boolean.valueOf(desc);
    }
}

