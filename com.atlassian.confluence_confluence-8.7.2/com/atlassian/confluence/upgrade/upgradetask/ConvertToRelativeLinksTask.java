/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.content.render.xhtml.links.XhtmlLinksUpdater;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertToRelativeLinksTask
implements BatchTask<ContentEntityObject> {
    private static final Logger log = LoggerFactory.getLogger(ConvertToRelativeLinksTask.class);
    private final XhtmlLinksUpdater linksUpdater;
    private final ContentDao contentDao;

    public ConvertToRelativeLinksTask(ContentDao contentDao, XhtmlLinksUpdater linksUpdater) {
        this.linksUpdater = linksUpdater;
        this.contentDao = contentDao;
    }

    @Override
    public boolean apply(ContentEntityObject entity, int index, int batchSize) throws CloneNotSupportedException {
        Object[] loggingParams = new String[]{String.valueOf(index + 1), String.valueOf(batchSize), entity.toString()};
        log.debug("({}/{}): Processing: {}", loggingParams);
        if (entity.getBodyContents().isEmpty() || !BodyType.XHTML.equals(entity.getBodyContent().getBodyType())) {
            return false;
        }
        String body = entity.getBodyAsString();
        if (StringUtils.isBlank((CharSequence)body) || !(entity instanceof SpaceContentEntityObject)) {
            return false;
        }
        String contracted = this.linksUpdater.contractAbsoluteReferencesInContent((SpaceContentEntityObject)entity);
        if (body.equals(contracted)) {
            return false;
        }
        ContentEntityObject entityClone = (ContentEntityObject)entity.clone();
        entity.setBodyAsString(contracted);
        Date originalLastModificationDate = entityClone.getLastModificationDate();
        if (originalLastModificationDate != null) {
            entity.setLastModificationDate(new Date(originalLastModificationDate.getTime() + 1000L));
        }
        entity.setLastModifier(entityClone.getLastModifier());
        entity.setVersionComment("Corrected links that should have been relative instead of absolute.");
        if (entity instanceof AbstractPage) {
            this.contentDao.save(entity, entityClone);
        } else {
            this.contentDao.save(entity);
        }
        log.debug("({}/{}): Resource identifiers converted for: {}", loggingParams);
        return true;
    }
}

