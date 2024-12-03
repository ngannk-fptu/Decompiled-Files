/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.jackrabbit.server.BasicCredentialsProvider;
import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.server.SessionProviderImpl;
import org.apache.jackrabbit.server.jcr.JCRWebdavServer;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.jcr.DavLocatorFactoryImpl;
import org.apache.jackrabbit.webdav.jcr.DavResourceFactoryImpl;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.observation.SubscriptionManagerImpl;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JCRWebdavServerServlet
extends AbstractWebdavServlet {
    private static Logger log = LoggerFactory.getLogger(JCRWebdavServerServlet.class);
    public static final String INIT_PARAM_RESOURCE_PATH_PREFIX = "resource-path-prefix";
    public static final String INIT_PARAM_CONCURRENCY_LEVEL = "concurrency-level";
    public static final String CTX_ATTR_RESOURCE_PATH_PREFIX = "jackrabbit.webdav.jcr.resourcepath";
    private String pathPrefix;
    private JCRWebdavServer server;
    private DavResourceFactory resourceFactory;
    private DavLocatorFactory locatorFactory;
    protected TxLockManagerImpl txMgr;
    protected SubscriptionManager subscriptionMgr;

    @Override
    public void init() throws ServletException {
        super.init();
        this.pathPrefix = this.getInitParameter(INIT_PARAM_RESOURCE_PATH_PREFIX);
        this.getServletContext().setAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX, (Object)this.pathPrefix);
        log.debug("resource-path-prefix = " + this.pathPrefix);
        this.txMgr = new TxLockManagerImpl();
        this.subscriptionMgr = new SubscriptionManagerImpl();
        this.txMgr.addTransactionListener((SubscriptionManagerImpl)this.subscriptionMgr);
        this.resourceFactory = new DavResourceFactoryImpl(this.txMgr, this.subscriptionMgr);
        this.locatorFactory = new DavLocatorFactoryImpl(this.pathPrefix);
    }

    @Override
    protected boolean isPreconditionValid(WebdavRequest request, DavResource resource) {
        if (!request.matchesIfHeader(resource)) {
            return false;
        }
        try {
            Session repositorySesssion = JcrDavSession.getRepositorySession(request.getDavSession());
            String reqWspName = resource.getLocator().getWorkspaceName();
            String wsName = repositorySesssion.getWorkspace().getName();
            if (27 != DavMethods.getMethodCode(request.getMethod()) && reqWspName != null && !reqWspName.equals(wsName)) {
                return false;
            }
        }
        catch (DavException e) {
            log.error("Internal error: " + e.toString());
            return false;
        }
        String txId = request.getTransactionId();
        return txId == null || this.txMgr.hasLock(txId, resource);
    }

    @Override
    public DavSessionProvider getDavSessionProvider() {
        if (this.server == null) {
            Repository repository = this.getRepository();
            String cl = this.getInitParameter(INIT_PARAM_CONCURRENCY_LEVEL);
            if (cl != null) {
                try {
                    this.server = new JCRWebdavServer(repository, this.getSessionProvider(), Integer.parseInt(cl));
                }
                catch (NumberFormatException e) {
                    log.debug("Invalid value '" + cl + "' for init-param 'concurrency-level'. Using default instead.");
                    this.server = new JCRWebdavServer(repository, this.getSessionProvider());
                }
            } else {
                this.server = new JCRWebdavServer(repository, this.getSessionProvider());
            }
        }
        return this.server;
    }

    @Override
    public void setDavSessionProvider(DavSessionProvider davSessionProvider) {
        throw new UnsupportedOperationException("Not implemented. DavSession(s) are provided by the 'JCRWebdavServer'");
    }

    @Override
    public DavLocatorFactory getLocatorFactory() {
        if (this.locatorFactory == null) {
            this.locatorFactory = new DavLocatorFactoryImpl(this.pathPrefix);
        }
        return this.locatorFactory;
    }

    @Override
    public void setLocatorFactory(DavLocatorFactory locatorFactory) {
        this.locatorFactory = locatorFactory;
    }

    @Override
    public DavResourceFactory getResourceFactory() {
        if (this.resourceFactory == null) {
            this.resourceFactory = new DavResourceFactoryImpl(this.txMgr, this.subscriptionMgr);
        }
        return this.resourceFactory;
    }

    @Override
    public void setResourceFactory(DavResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    @Override
    protected int validateDestination(DavResource destResource, WebdavRequest request, boolean checkHeader) throws DavException {
        int status;
        String destHeader;
        if (checkHeader && ((destHeader = request.getHeader("Destination")) == null || "".equals(destHeader))) {
            return 400;
        }
        if (destResource.getLocator().equals(request.getRequestLocator())) {
            return 403;
        }
        if (destResource.exists()) {
            if (request.isOverwrite()) {
                if (!request.matchesIfHeader(destResource)) {
                    return 412;
                }
                destResource.getCollection().removeMember(destResource);
                status = 204;
            } else {
                status = 201;
            }
        } else {
            status = 201;
        }
        return status;
    }

    public static String getPathPrefix(ServletContext ctx) {
        return (String)ctx.getAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX);
    }

    protected abstract Repository getRepository();

    protected CredentialsProvider getCredentialsProvider() {
        return new BasicCredentialsProvider(this.getInitParameter("missing-auth-mapping"));
    }

    protected SessionProvider getSessionProvider() {
        return new SessionProviderImpl(this.getCredentialsProvider());
    }
}

