/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator$MigrationResult
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.renderer.RenderContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap.upgradetask;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.renderer.RenderContext;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
    private static Logger log = LoggerFactory.getLogger(Helper.class);

    public static boolean migrate(ContentEntityObject entity, ExceptionTolerantMigrator migrator, ContentEntityManager entityManager) throws CloneNotSupportedException, RuntimeException {
        DefaultConversionContext conversionContext;
        BodyContent bodyContent = entity.getBodyContent();
        String originalBodyText = bodyContent == null ? "" : bodyContent.getBody();
        ExceptionTolerantMigrator.MigrationResult migrationResult = migrator.migrate(originalBodyText, (ConversionContext)(conversionContext = new DefaultConversionContext((RenderContext)entity.toPageContext())));
        if (migrationResult == null || !migrationResult.isMigrationPerformed()) {
            log.debug("No migration was performed for content entity {} with title '{}'", (Object)entity.getIdAsString(), (Object)entity.getTitle());
            return false;
        }
        ContentEntityObject originalVersion = (ContentEntityObject)entity.clone();
        String contentMigrationResult = migrationResult.getContent();
        entity.setBodyAsString(contentMigrationResult);
        Helper.doMigrate(entity, originalVersion, entityManager);
        return true;
    }

    private static void doMigrate(ContentEntityObject newEntity, ContentEntityObject originalEntity, ContentEntityManager entityManager) {
        Date originalLastModificationDate = originalEntity.getLastModificationDate();
        if (originalLastModificationDate != null) {
            newEntity.setLastModificationDate(new Date(originalLastModificationDate.getTime() + 1000L));
        }
        AuthenticatedUserThreadLocal.set((ConfluenceUser)originalEntity.getLastModifier());
        newEntity.setLastModifier(originalEntity.getLastModifier());
        newEntity.setVersionComment("Upgrade Task: Roadmap's unused parameter removed.");
        if (newEntity instanceof Draft) {
            Draft draft = (Draft)newEntity;
            draft.setPageVersion(draft.getPageVersion() + 1);
        }
        DefaultSaveContext saveContext = new DefaultSaveContext(true, true, true);
        if (newEntity instanceof Page || newEntity instanceof BlogPost) {
            entityManager.saveContentEntity(newEntity, originalEntity, (SaveContext)saveContext);
        } else {
            entityManager.saveContentEntity(newEntity, (SaveContext)saveContext);
        }
    }
}

