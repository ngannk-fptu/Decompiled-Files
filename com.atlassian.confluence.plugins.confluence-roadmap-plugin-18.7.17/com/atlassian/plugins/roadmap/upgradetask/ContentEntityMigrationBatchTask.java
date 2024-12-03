/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.migration.BatchException
 *  com.atlassian.confluence.content.render.xhtml.migration.BatchTask
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap.upgradetask;

import com.atlassian.confluence.content.render.xhtml.migration.BatchException;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.plugins.roadmap.upgradetask.Helper;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ContentEntityMigrationBatchTask
implements BatchTask<ContentEntityObject> {
    private static Logger log = LoggerFactory.getLogger(ContentEntityMigrationBatchTask.class);
    private final ExceptionTolerantMigrator migrator;
    private final ContentEntityManager entityManager;

    public ContentEntityMigrationBatchTask(ExceptionTolerantMigrator migrator, ContentEntityManager entityManager) {
        this.migrator = migrator;
        this.entityManager = entityManager;
    }

    public boolean apply(ContentEntityObject entity, int index, int batchSize) throws BatchException, CloneNotSupportedException {
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        boolean migrateResult = false;
        try {
            migrateResult = Helper.migrate(entity, this.migrator, this.entityManager);
        }
        catch (RuntimeException e) {
            exceptions.add(e);
        }
        if (!exceptions.isEmpty()) {
            throw new BatchException(exceptions);
        }
        return migrateResult;
    }
}

