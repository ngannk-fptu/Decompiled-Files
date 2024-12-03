/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration.macro;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.BatchException;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentEntityMigrationBatchTask
implements BatchTask<ContentEntityObject> {
    private static Logger log = LoggerFactory.getLogger(ContentEntityMigrationBatchTask.class);
    private final ExceptionTolerantMigrator migrator;
    private final ContentDao contentDao;
    private final String versionComment;

    public ContentEntityMigrationBatchTask(ExceptionTolerantMigrator migrator, ContentDao contentDao, String versionComment) {
        this.migrator = migrator;
        this.contentDao = contentDao;
        this.versionComment = versionComment;
    }

    @Override
    public boolean apply(ContentEntityObject entity, int index, int batchSize) throws BatchException, CloneNotSupportedException {
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        log.debug("Migrating content of type '{}' with title '{}'", (Object)entity.getType(), (Object)entity.getTitle());
        BodyContent bodyContent = entity.getBodyContent();
        String originalBodyText = bodyContent == null ? "" : bodyContent.getBody();
        DefaultConversionContext conversionContext = new DefaultConversionContext(entity.toPageContext());
        ExceptionTolerantMigrator.MigrationResult migrationResult = this.migrator.migrate(originalBodyText, conversionContext);
        if (migrationResult == null || !migrationResult.isMigrationPerformed()) {
            log.debug("No migration was performed for content entity {} with title '{}'", (Object)entity.getIdAsString(), (Object)entity.getTitle());
            return false;
        }
        try {
            ContentEntityObject originalVersion = (ContentEntityObject)entity.clone();
            bodyContent.setBodyType(BodyType.XHTML);
            bodyContent.setBody(migrationResult.getContent());
            entity.setBodyContent(bodyContent);
            this.doMigrate(entity, originalVersion);
        }
        catch (RuntimeException e) {
            log.error("{}: Unable to set body for entity: {} - With Exception Message: {}", new Object[]{Thread.currentThread().getName(), entity.toString(), e.getMessage()});
            exceptions.add(e);
        }
        if (!exceptions.isEmpty()) {
            throw new BatchException(exceptions);
        }
        return true;
    }

    private void doMigrate(ContentEntityObject newEntity, ContentEntityObject originalEntity) {
        Date originalLastModificationDate = originalEntity.getLastModificationDate();
        if (originalLastModificationDate != null) {
            newEntity.setLastModificationDate(new Date(originalLastModificationDate.getTime() + 1000L));
        }
        newEntity.setLastModifier(originalEntity.getLastModifier());
        newEntity.setVersionComment(this.versionComment);
        if (newEntity instanceof Draft) {
            Draft draft = (Draft)newEntity;
            draft.setPageVersion(draft.getPageVersion() + 1);
        }
        if (newEntity instanceof Page || newEntity instanceof BlogPost) {
            this.contentDao.save(newEntity, originalEntity);
        } else {
            this.contentDao.save(newEntity);
        }
    }
}

