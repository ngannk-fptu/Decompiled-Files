/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  net.java.ao.Entity
 *  net.java.ao.Query
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.db;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.collaborative.content.feedback.db.Utils;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.java.ao.Entity;
import net.java.ao.Query;

public class AbstractHistoryDao<T extends Entity> {
    protected final ActiveObjects ao;
    protected final Utils dbUtils;

    public AbstractHistoryDao(ActiveObjects ao, Utils dbUtils) {
        this.ao = ao;
        this.dbUtils = dbUtils;
    }

    public List<T> history(long contentId, int offset, int limit) {
        return this.dbUtils.executeInTransaction(true, contentId, () -> Arrays.asList((Entity[])this.ao.find(this.getAoEntityClass(), Query.select().where(this.dbUtils.escapeIdentifier("CONTENT_ID") + " = ?", new Object[]{contentId}).offset(offset).limit(limit))));
    }

    public void cleanUp(Date olderThan) {
        this.dbUtils.executeInTransaction(false, 0L, () -> {
            this.ao.deleteWithSQL(this.getAoEntityClass(), this.dbUtils.escapeIdentifier("INSERTED") + " < ?", new Object[]{olderThan});
            this.ao.flushAll();
            return null;
        });
    }

    private Class<T> getAoEntityClass() {
        ParameterizedType aoEntityType = (ParameterizedType)this.getClass().getGenericSuperclass();
        return (Class)aoEntityType.getActualTypeArguments()[0];
    }
}

