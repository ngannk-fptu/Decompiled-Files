/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.SessionInfo;

public class SessionInfoImpl
implements SessionInfo,
Serializable {
    private String userId;
    private String userData;
    private String workspaceName;
    private List<String> lockTokens = new ArrayList<String>();

    public void setUserID(String userId) {
        this.userId = userId;
    }

    public void setWorkspacename(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String getUserID() {
        return this.userId;
    }

    @Override
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    @Override
    public String[] getLockTokens() {
        return this.lockTokens.toArray(new String[this.lockTokens.size()]);
    }

    @Override
    public void addLockToken(String s) {
        this.lockTokens.add(s);
    }

    @Override
    public void removeLockToken(String s) {
        this.lockTokens.remove(s);
    }

    @Override
    public void setUserData(String userData) throws RepositoryException {
        this.userData = userData;
    }

    public String getUserData() {
        return this.userData;
    }
}

