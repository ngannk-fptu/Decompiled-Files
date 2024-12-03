/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.spaces;

import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SpaceRemovalLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private static final Logger log = LoggerFactory.getLogger(SpaceRemovalLongRunningTask.class);
    private final String spaceKey;
    private final SpaceManager spaceManager;
    private final ConfluenceUser user;
    private final I18NBean i18n;

    public SpaceRemovalLongRunningTask(String spaceKey, SpaceManager spaceManager, ConfluenceUser user, I18NBean i18n) {
        this.spaceKey = spaceKey;
        this.spaceManager = spaceManager;
        this.user = user;
        this.i18n = i18n;
    }

    @Override
    protected void runInternal() {
        try (Cleanup cleanup = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            AuthenticatedUserThreadLocal.set(this.user);
            if (!this.spaceManager.removeSpace(this.spaceKey, this.progress).booleanValue()) {
                this.progress.setStatus(this.i18n.getText("progress.remove.space.failed"));
                this.progress.setCompletedSuccessfully(false);
                log.warn("Removing the space with key '{}' failed", (Object)this.spaceKey);
            } else {
                log.info("Successfully removed the space with key '{}'", (Object)this.spaceKey);
            }
        }
    }

    public String getName() {
        return "Space removal long running task";
    }
}

