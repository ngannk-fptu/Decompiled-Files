/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nullable
 *  net.java.ao.DBParam
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.AbstractHistoryDao;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.Utils;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.ao.Reconciliations;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;
import net.java.ao.DBParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationHistoryDao
extends AbstractHistoryDao<Reconciliations> {
    @Autowired
    public ReconciliationHistoryDao(@ComponentImport ActiveObjects ao, Utils dbUtils) {
        super(ao, dbUtils);
    }

    public void add(long contentId, String eventType, @Nullable String ancestor, @Nullable String revision, @Nullable String trigger) {
        this.dbUtils.executeInTransaction(false, contentId, () -> {
            Reconciliations entity = (Reconciliations)this.ao.create(Reconciliations.class, new DBParam[]{new DBParam("CONTENT_ID", (Object)contentId), new DBParam("EVENT_TYPE", (Object)Objects.requireNonNull(eventType)), new DBParam("ANCESTOR", (Object)ancestor), new DBParam("REVISION", (Object)revision), new DBParam("TRIGGER", (Object)trigger), new DBParam("INSERTED", (Object)new Date())});
            entity.save();
            return null;
        });
    }
}

