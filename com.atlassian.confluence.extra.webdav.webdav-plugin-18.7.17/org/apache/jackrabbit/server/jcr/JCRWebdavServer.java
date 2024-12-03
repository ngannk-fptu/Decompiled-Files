/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.jcr;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.servlet.ServletException;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.spi.commons.SessionExtensions;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.header.IfHeader;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.util.LinkHeaderFieldParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JCRWebdavServer
implements DavSessionProvider {
    private static Logger log = LoggerFactory.getLogger(JCRWebdavServer.class);
    private final SessionCache cache;
    private final Repository repository;
    private final SessionProvider sessionProvider;

    public JCRWebdavServer(Repository repository, SessionProvider sessionProvider) {
        this.repository = repository;
        this.sessionProvider = sessionProvider;
        this.cache = new SessionCache();
    }

    public JCRWebdavServer(Repository repository, SessionProvider sessionProvider, int concurrencyLevel) {
        this.repository = repository;
        this.sessionProvider = sessionProvider;
        this.cache = new SessionCache(concurrencyLevel);
    }

    @Override
    public boolean attachSession(WebdavRequest request) throws DavException {
        DavSession session = this.cache.get(request);
        request.setDavSession(session);
        return true;
    }

    @Override
    public void releaseSession(WebdavRequest request) {
        DavSession session = request.getDavSession();
        if (session != null) {
            session.removeReference(request);
        }
        request.setDavSession(null);
    }

    private class SessionCache {
        private static final int CONCURRENCY_LEVEL_DEFAULT = 50;
        private static final int INITIAL_CAPACITY = 50;
        private static final int INITIAL_CAPACITY_REF_TO_SESSION = 150;
        private ConcurrentMap<DavSession, Set<Object>> sessionMap;
        private ConcurrentMap<Object, DavSession> referenceToSessionMap;

        private SessionCache() {
            this(50);
        }

        private SessionCache(int cacheConcurrencyLevel) {
            this.sessionMap = new ConcurrentHashMap<DavSession, Set<Object>>(50, 0.75f, cacheConcurrencyLevel);
            this.referenceToSessionMap = new ConcurrentHashMap<Object, DavSession>(150, 0.75f, cacheConcurrencyLevel);
        }

        private DavSession get(WebdavRequest request) throws DavException {
            String txId = request.getTransactionId();
            String subscriptionId = request.getSubscriptionId();
            String lockToken = request.getLockToken();
            DavSession session = null;
            if (lockToken != null && this.containsReference(lockToken)) {
                session = this.getSessionByReference(lockToken);
            } else if (txId != null && this.containsReference(txId)) {
                session = this.getSessionByReference(txId);
            } else if (subscriptionId != null && this.containsReference(subscriptionId)) {
                session = this.getSessionByReference(subscriptionId);
            }
            if (session == null) {
                IfHeader ifHeader = new IfHeader(request);
                Iterator<String> it = ifHeader.getAllTokens();
                while (it.hasNext()) {
                    String token = it.next();
                    if (!this.containsReference(token)) continue;
                    session = this.getSessionByReference(token);
                    break;
                }
            }
            if (session == null) {
                Session repSession = this.getRepositorySession(request);
                session = new DavSessionImpl(repSession);
                this.sessionMap.put(session, new HashSet());
                log.debug("login: User '" + repSession.getUserID() + "' logged in.");
            } else {
                log.debug("login: Retrieved cached session for user '" + this.getUserID(session) + "'");
            }
            this.addReference(session, request);
            return session;
        }

        private void addReference(DavSession session, Object reference) {
            Set referenceSet = (Set)this.sessionMap.get(session);
            if (referenceSet != null) {
                referenceSet.add(reference);
                this.referenceToSessionMap.put(reference, session);
            } else {
                log.error("Failed to add reference to session. No entry in cache found.");
            }
        }

        private void removeReference(DavSession session, Object reference) {
            Set referenceSet = (Set)this.sessionMap.get(session);
            if (referenceSet != null) {
                if (referenceSet.remove(reference)) {
                    log.debug("Removed reference " + reference + " to session " + session);
                    this.referenceToSessionMap.remove(reference);
                } else {
                    log.warn("Failed to remove reference " + reference + " to session " + session);
                }
                if (referenceSet.isEmpty()) {
                    log.debug("No more references present on webdav session -> clean up.");
                    this.sessionMap.remove(session);
                    try {
                        Session repSession = DavSessionImpl.getRepositorySession(session);
                        String usr = this.getUserID(session);
                        JCRWebdavServer.this.sessionProvider.releaseSession(repSession);
                        log.debug("Login: User '" + usr + "' logged out");
                    }
                    catch (DavException e) {
                        log.error("Unexpected error: " + e.getMessage(), e.getCause());
                    }
                } else {
                    log.debug(referenceSet.size() + " references remaining on webdav session " + session);
                }
            } else {
                log.error("Failed to remove reference from session. No entry in cache found.");
            }
        }

        private boolean containsReference(Object reference) {
            return this.referenceToSessionMap.containsKey(reference);
        }

        private DavSession getSessionByReference(Object reference) {
            return (DavSession)this.referenceToSessionMap.get(reference);
        }

        private Session getRepositorySession(WebdavRequest request) throws DavException {
            try {
                String workspaceName = null;
                if (27 != DavMethods.getMethodCode(request.getMethod())) {
                    workspaceName = request.getRequestLocator().getWorkspaceName();
                }
                Session session = JCRWebdavServer.this.sessionProvider.getSession(request, JCRWebdavServer.this.repository, workspaceName);
                LinkHeaderFieldParser lhfp = new LinkHeaderFieldParser(request.getHeaders("Link"));
                this.setJcrUserData(session, lhfp);
                this.setSessionIdentifier(session, lhfp);
                return session;
            }
            catch (LoginException e) {
                throw new JcrDavException(e);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
            catch (ServletException e) {
                throw new DavException(500);
            }
        }

        private void setJcrUserData(Session session, LinkHeaderFieldParser lhfp) throws RepositoryException {
            String data = null;
            String target = lhfp.getFirstTargetForRelation("http://www.day.com/jcr/webdav/1.0/user-data");
            if (target != null) {
                try {
                    String sspart;
                    URI uri = new URI(target);
                    if ("data".equalsIgnoreCase(uri.getScheme()) && (sspart = uri.getRawSchemeSpecificPart()).startsWith(",")) {
                        data = Text.unescape(sspart.substring(1));
                    }
                }
                catch (URISyntaxException uRISyntaxException) {
                    // empty catch block
                }
            }
            try {
                session.getWorkspace().getObservationManager().setUserData(data);
            }
            catch (UnsupportedRepositoryOperationException unsupportedRepositoryOperationException) {
                // empty catch block
            }
        }

        private void setSessionIdentifier(Session session, LinkHeaderFieldParser lhfp) {
            if (session instanceof SessionExtensions) {
                String name = "http://www.day.com/jcr/webdav/1.0/session-id";
                String id = lhfp.getFirstTargetForRelation(name);
                ((SessionExtensions)((Object)session)).setAttribute(name, id);
            }
        }

        private String getUserID(DavSession session) {
            try {
                Session s = DavSessionImpl.getRepositorySession(session);
                if (s != null) {
                    return s.getUserID();
                }
            }
            catch (DavException e) {
                log.error(e.toString());
            }
            return session.toString();
        }
    }

    private class DavSessionImpl
    extends JcrDavSession {
        private DavSessionImpl(Session session) {
            super(session);
        }

        @Override
        public void addReference(Object reference) {
            JCRWebdavServer.this.cache.addReference(this, reference);
        }

        @Override
        public void removeReference(Object reference) {
            JCRWebdavServer.this.cache.removeReference(this, reference);
        }
    }
}

