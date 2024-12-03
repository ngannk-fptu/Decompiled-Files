/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.util.HashSet;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.jcr.lock.LockTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JcrDavSession
implements DavSession {
    private static Logger log = LoggerFactory.getLogger(JcrDavSession.class);
    private final Session session;
    private final HashSet<String> lockTokens = new HashSet();

    protected JcrDavSession(Session session) {
        this.session = session;
    }

    public static void checkImplementation(DavSession davSession) throws DavException {
        if (!(davSession instanceof JcrDavSession)) {
            throw new DavException(500, "JCR specific DavSession expected. Found: " + davSession);
        }
    }

    public static Session getRepositorySession(DavSession davSession) throws DavException {
        JcrDavSession.checkImplementation(davSession);
        return ((JcrDavSession)davSession).getRepositorySession();
    }

    public Session getRepositorySession() {
        return this.session;
    }

    @Override
    public void addLockToken(String token) {
        if (!LockTokenMapper.isForSessionScopedLock(token)) {
            try {
                this.session.getWorkspace().getLockManager().addLockToken(LockTokenMapper.getJcrLockToken(token));
            }
            catch (RepositoryException ex) {
                log.debug("trying to add lock token " + token + " to session", (Throwable)ex);
            }
        }
        this.lockTokens.add(token);
    }

    @Override
    public String[] getLockTokens() {
        return this.lockTokens.toArray(new String[this.lockTokens.size()]);
    }

    @Override
    public void removeLockToken(String token) {
        if (!LockTokenMapper.isForSessionScopedLock(token)) {
            try {
                this.session.getWorkspace().getLockManager().removeLockToken(LockTokenMapper.getJcrLockToken(token));
            }
            catch (RepositoryException ex) {
                log.debug("trying to remove lock token " + token + " to session", (Throwable)ex);
            }
        }
        this.lockTokens.remove(token);
    }
}

