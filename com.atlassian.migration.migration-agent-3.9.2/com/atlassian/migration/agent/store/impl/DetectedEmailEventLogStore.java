/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityExistsException
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.DetectedEmailEventLog;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityExistsException;
import org.slf4j.Logger;

public class DetectedEmailEventLogStore {
    private static final Logger log = ContextLoggerFactory.getLogger(DetectedEmailEventLogStore.class);
    private final EntityManagerTemplate tmpl;

    public DetectedEmailEventLogStore(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    public void track(String email, String cloudId) {
        DetectedEmailEventLog entry = new DetectedEmailEventLog(cloudId, email);
        try {
            this.tmpl.persist(entry);
        }
        catch (EntityExistsException ex) {
            log.warn("{} already exists in DB. Skipping creation...", (Object)entry);
        }
    }

    public Set<String> findEmailsWhichAreNotTracked(String cloudId, Set<String> emails) {
        List<String> trackedEmails = this.tmpl.query(String.class, "select a.email from DetectedEmailEventLog a where a.cloudId = :cloudId and a.email in (:emails)").param("cloudId", (Object)cloudId).param("emails", emails).list();
        HashSet<String> result = new HashSet<String>(emails);
        result.removeAll(trackedEmails);
        return result;
    }
}

