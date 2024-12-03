/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractUserMappingUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private final ConfluenceUserDao confluenceUserDao;
    private final BatchOperationManager batchOperationManager;

    public AbstractUserMappingUpgradeTask(ConfluenceUserDao confluenceUserDao, BatchOperationManager batchOperationManager) {
        this.confluenceUserDao = confluenceUserDao;
        this.batchOperationManager = batchOperationManager;
    }

    public void doUpgrade() throws Exception {
        try {
            log.info("Beginning user mapping creation.");
            log.info("Retrieving usernames from the database.");
            Set<String> missingMappingNames = this.getUsernamesMissingMapping();
            log.info("Found " + missingMappingNames.size() + " unique usernames that need mapping created.");
            this.batchOperationManager.applyInBatches(missingMappingNames, missingMappingNames.size(), new Function<String, Void>(){

                @Override
                public Void apply(String username) {
                    AbstractUserMappingUpgradeTask.this.confluenceUserDao.create(new ConfluenceUserImpl((User)new DefaultUser(username)));
                    return null;
                }

                public String toString() {
                    return "ConfluenceUser creation";
                }
            });
            log.info("Finished user mapping creation.");
        }
        catch (RuntimeException ex) {
            throw new RuntimeException("Unable to complete user mapping creation.", ex);
        }
    }

    protected abstract Set<String> getUsernamesMissingMapping();
}

