/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.ResourceStates;
import com.atlassian.confluence.extra.webdav.util.UserAgentUtil;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SerializableLockManager;

public class ConfluenceDavSession
implements DavSession,
Serializable {
    private String userName;
    private ResourceStates resourceStates;
    private Set<String> lockTokens;
    private LockManager lockManager;
    private long lastActivityTimestamp;
    private String userAgent;
    private boolean currentlyBeingUsed;

    public ConfluenceDavSession(String userName) {
        this.userName = userName;
        this.resourceStates = new ResourceStates();
        this.lockTokens = new HashSet<String>();
        this.lockManager = new SerializableLockManager();
        this.updateActivityTimestamp();
    }

    protected ConfluenceDavSession() {
    }

    public String getUserName() {
        return this.userName;
    }

    public ResourceStates getResourceStates() {
        return this.resourceStates;
    }

    public LockManager getLockManager() {
        return this.lockManager;
    }

    public void updateActivityTimestamp() {
        this.lastActivityTimestamp = System.currentTimeMillis();
    }

    public long getLastActivityTimestamp() {
        return this.lastActivityTimestamp;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isCurrentlyBeingUsed() {
        return this.currentlyBeingUsed;
    }

    public void setCurrentlyBeingUsed(boolean currentlyBeingUsed) {
        this.currentlyBeingUsed = currentlyBeingUsed;
    }

    public boolean isClientFinder() {
        return UserAgentUtil.isOsxFinder(this.userAgent);
    }

    @Override
    public void addReference(Object o) {
    }

    @Override
    public void removeReference(Object o) {
    }

    @Override
    public void addLockToken(String s) {
        this.lockTokens.add(s);
    }

    @Override
    public String[] getLockTokens() {
        return this.lockTokens.toArray(new String[0]);
    }

    @Override
    public void removeLockToken(String s) {
        this.lockTokens.remove(s);
    }
}

