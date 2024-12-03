/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.simple.DavSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DavSessionProviderImpl
implements DavSessionProvider {
    private static Logger log = LoggerFactory.getLogger(DavSessionProviderImpl.class);
    private final Repository repository;
    private final SessionProvider sesProvider;

    public DavSessionProviderImpl(Repository rep, SessionProvider sesProvider) {
        this.repository = rep;
        this.sesProvider = sesProvider;
    }

    @Override
    public boolean attachSession(WebdavRequest request) throws DavException {
        try {
            Session repSession;
            String workspaceName = request.getRequestLocator().getWorkspaceName();
            if (workspaceName != null && "".equals(workspaceName)) {
                workspaceName = null;
            }
            if ((repSession = this.sesProvider.getSession(request, this.repository, workspaceName)) == null) {
                log.debug("Could not to retrieve a repository session.");
                return false;
            }
            DavSessionImpl ds = new DavSessionImpl(repSession);
            log.debug("Attaching session '" + ds + "' to request '" + request + "'");
            request.setDavSession(ds);
            return true;
        }
        catch (NoSuchWorkspaceException e) {
            throw new JcrDavException(e, 404);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
        catch (ServletException e) {
            throw new DavException(500, e.getMessage());
        }
    }

    @Override
    public void releaseSession(WebdavRequest request) {
        DavSession ds = request.getDavSession();
        if (ds != null && ds instanceof DavSessionImpl) {
            Session repSession = ((DavSessionImpl)ds).getRepositorySession();
            for (String lockToken : repSession.getLockTokens()) {
                repSession.removeLockToken(lockToken);
            }
            this.sesProvider.releaseSession(repSession);
            log.debug("Releasing session '" + ds + "' from request '" + request + "'");
        }
        request.setDavSession(null);
    }
}

