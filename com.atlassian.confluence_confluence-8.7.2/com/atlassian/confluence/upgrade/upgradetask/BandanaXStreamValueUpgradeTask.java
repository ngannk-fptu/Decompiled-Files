/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.collect.Iterables
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.google.common.collect.Iterables;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class BandanaXStreamValueUpgradeTask
extends AbstractDeferredRunUpgradeTask
implements DatabaseUpgradeTask {
    private final BandanaManager bandanaManager;
    private final HibernateTemplate hibernateTemplate;
    private final BatchOperationManager batchOperationManager;

    public BandanaXStreamValueUpgradeTask(BandanaManager bandanaManager, SessionFactory sessionFactory, BatchOperationManager batchOperationManager) {
        this(bandanaManager, new HibernateTemplate(sessionFactory), batchOperationManager);
    }

    @Internal
    @VisibleForTesting
    BandanaXStreamValueUpgradeTask(BandanaManager bandanaManager, HibernateTemplate hibernateTemplate, BatchOperationManager batchOperationManager) {
        this.bandanaManager = bandanaManager;
        this.hibernateTemplate = hibernateTemplate;
        this.batchOperationManager = batchOperationManager;
    }

    public void doDeferredUpgrade() {
        log.info("Starting Bandana XStream Content Migration for 1.1.1 format");
        List<String> contexts = this.findContexts();
        log.info("Migrating {} context(s) in Bandana", (Object)contexts.size());
        this.migrateBandanaContext(ConfluenceBandanaContext.GLOBAL_CONTEXT);
        contexts.stream().filter(context -> !ConfluenceBandanaContext.GLOBAL_CONTEXT.getContextKey().equals(context)).forEach(context -> this.migrateBandanaContext(new ConfluenceBandanaContext((String)context)));
        log.info("Finished Bandana XStream Content Migration");
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }

    public String getBuildNumber() {
        return "8506";
    }

    public String getShortDescription() {
        return "Migrate Bandana values stored with XStream 1.1.1 to XStream 1.4.x";
    }

    private void migrateBandanaContext(ConfluenceBandanaContext context) {
        Iterable keys = this.bandanaManager.getKeys((BandanaContext)context);
        this.batchOperationManager.applyInBatches(keys, Iterables.size((Iterable)keys), key -> {
            this.migrateDataForKey(context, (String)key);
            return null;
        });
    }

    private void migrateDataForKey(ConfluenceBandanaContext context, String key) {
        try {
            Object value = this.bandanaManager.getValue((BandanaContext)context, key);
            if (value != null) {
                this.bandanaManager.setValue((BandanaContext)context, key, value);
            }
        }
        catch (Exception e) {
            log.error("Couldn't migrate key '{}' for context '{}' in Bandana as corresponding Class is not available or serialization failed", new Object[]{key, context, e});
        }
    }

    private List<String> findContexts() {
        return (List)this.hibernateTemplate.execute(session -> {
            Query query = session.createQuery("select distinct cbr.context from ConfluenceBandanaRecord cbr");
            return query.list();
        });
    }
}

