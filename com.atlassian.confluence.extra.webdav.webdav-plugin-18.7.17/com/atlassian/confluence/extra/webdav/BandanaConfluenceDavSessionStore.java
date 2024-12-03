/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionStore;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionTask;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={ConfluenceDavSessionStore.class})
@ParametersAreNonnullByDefault
public class BandanaConfluenceDavSessionStore
implements ConfluenceDavSessionStore {
    private static final Logger logger = LoggerFactory.getLogger(BandanaConfluenceDavSessionStore.class);
    private final BandanaContext bandanaContext = new ConfluenceBandanaContext("com.atlassian.confluence.extra.webdav.sessions");
    private final BandanaManager bandanaManager;
    private final long sessionTimeoutInMillis;
    private static final long DEFAULT_SESSION_TIMEOUT = 1800000L;

    @VisibleForTesting
    BandanaConfluenceDavSessionStore(BandanaManager bandanaManager, long sessionTimeoutInMillis) {
        this.bandanaManager = bandanaManager;
        this.sessionTimeoutInMillis = sessionTimeoutInMillis;
    }

    @Autowired
    public BandanaConfluenceDavSessionStore(@ComponentImport BandanaManager bandanaManager) {
        this(bandanaManager, 1800000L);
    }

    @Override
    public void mapSession(ConfluenceDavSession davSession, String userName) {
        this.bandanaManager.setValue(this.bandanaContext, userName, (Object)davSession);
    }

    @Override
    @Nullable
    public ConfluenceDavSession getSession(String userName) {
        return (ConfluenceDavSession)this.bandanaManager.getValue(this.bandanaContext, userName, false);
    }

    private boolean isSessionExpired(ConfluenceDavSession confluenceDavSession) {
        return System.currentTimeMillis() - confluenceDavSession.getLastActivityTimestamp() > this.sessionTimeoutInMillis && !confluenceDavSession.isCurrentlyBeingUsed();
    }

    @Override
    public void invalidateExpiredSessions() {
        Set<String> uniqueSessionKeys = this.getSessionKeys();
        int sessionSize = uniqueSessionKeys.size();
        if (sessionSize > 100) {
            logger.warn("There are " + sessionSize + " active WebDAV sessions just before invalidation. Just thought of telling you that because there seems to be an unusual number of users using the WebDAV plugin.");
        }
        for (String sessionKey : uniqueSessionKeys) {
            ConfluenceDavSession confluenceDavSession = this.getSession(sessionKey);
            if (null == confluenceDavSession || !this.isSessionExpired(confluenceDavSession)) continue;
            this.bandanaManager.removeValue(this.bandanaContext, sessionKey);
        }
    }

    private Set<String> getSessionKeys() {
        HashSet<String> keys = new HashSet<String>();
        for (String key : this.bandanaManager.getKeys(this.bandanaContext)) {
            keys.add(key);
        }
        return keys;
    }

    @Override
    public void executeTaskOnSessions(ConfluenceDavSessionTask confluenceDavSessionTask) {
        Set<String> uniqueSessionKeys = this.getSessionKeys();
        for (String sessionKey : uniqueSessionKeys) {
            ConfluenceDavSession confluenceDavSession = this.getSession(sessionKey);
            confluenceDavSessionTask.execute(confluenceDavSession);
            if (confluenceDavSession == null) continue;
            this.mapSession(confluenceDavSession, confluenceDavSession.getUserName());
        }
    }
}

