/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.SessionInfo;
import org.apache.jackrabbit.spi.commons.logging.AbstractLogger;
import org.apache.jackrabbit.spi.commons.logging.LogWriter;

public class SessionInfoLogger
extends AbstractLogger
implements SessionInfo {
    private final SessionInfo sessionInfo;

    public SessionInfoLogger(SessionInfo sessionInfo, LogWriter writer) {
        super(writer);
        this.sessionInfo = sessionInfo;
    }

    public SessionInfo getSessionInfo() {
        return this.sessionInfo;
    }

    @Override
    public String getUserID() {
        return (String)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return SessionInfoLogger.this.sessionInfo.getUserID();
            }
        }, "getUserID()", new Object[0]);
    }

    @Override
    public String getWorkspaceName() {
        return (String)this.execute(new AbstractLogger.SafeCallable(){

            @Override
            public Object call() {
                return SessionInfoLogger.this.sessionInfo.getWorkspaceName();
            }
        }, "getWorkspaceName()", new Object[0]);
    }

    @Override
    public String[] getLockTokens() throws RepositoryException {
        return (String[])this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                return SessionInfoLogger.this.sessionInfo.getLockTokens();
            }
        }, "getLockTokens()", new Object[0]);
    }

    @Override
    public void addLockToken(final String lockToken) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                SessionInfoLogger.this.sessionInfo.addLockToken(lockToken);
                return null;
            }
        }, "addLockToken(String)", new Object[]{lockToken});
    }

    @Override
    public void removeLockToken(final String lockToken) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                SessionInfoLogger.this.sessionInfo.removeLockToken(lockToken);
                return null;
            }
        }, "removeLockToken(String)", new Object[]{lockToken});
    }

    @Override
    public void setUserData(final String userData) throws RepositoryException {
        this.execute(new AbstractLogger.Callable(){

            @Override
            public Object call() throws RepositoryException {
                SessionInfoLogger.this.sessionInfo.setUserData(userData);
                return null;
            }
        }, "setUserData(String)", new Object[]{userData});
    }
}

