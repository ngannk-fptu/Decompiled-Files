/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  javax.inject.Inject
 *  javax.inject.Named
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.db;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.db.model.SeenAssertion;
import com.google.common.base.Preconditions;
import java.time.Instant;
import javax.inject.Inject;
import javax.inject.Named;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SeenAssertionDao {
    private final ActiveObjects ao;
    private static final Logger log = LoggerFactory.getLogger(SeenAssertionDao.class);

    @Inject
    public SeenAssertionDao(@ComponentImport ActiveObjects ao) {
        this.ao = ao;
    }

    public boolean assertionIdExists(String assertionId) {
        SeenAssertion[] assertions = (SeenAssertion[])this.ao.find(SeenAssertion.class, Query.select().where(String.format("%s = ?", "ASSERTION_ID"), new Object[]{assertionId}));
        Preconditions.checkState((assertions.length <= 1 ? 1 : 0) != 0, (Object)("Should have 0 or 1 seen assertions with id " + assertionId + " but founds " + assertions.length));
        return assertions.length == 1;
    }

    public void saveAssertionId(String assertionId, Instant expiryTimestamp) {
        this.ao.executeInTransaction(() -> {
            this.ao.create(SeenAssertion.class, new DBParam[]{new DBParam("ASSERTION_ID", (Object)assertionId), new DBParam("EXPIRY_TIMESTAMP", (Object)expiryTimestamp.toEpochMilli())});
            return null;
        });
        log.debug("Saved seen assertion {}, expires on {}", (Object)assertionId, (Object)expiryTimestamp);
    }

    public void removeOlderThan(Instant timestamp) {
        this.ao.executeInTransaction(() -> {
            int removed = this.ao.deleteWithSQL(SeenAssertion.class, "EXPIRY_TIMESTAMP < ?", new Object[]{timestamp.toEpochMilli()});
            log.debug("Deleted {} assertions older than {}", (Object)removed, (Object)timestamp);
            return null;
        });
    }
}

