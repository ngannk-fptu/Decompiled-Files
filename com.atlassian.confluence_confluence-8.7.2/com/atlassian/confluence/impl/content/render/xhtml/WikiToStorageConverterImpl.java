/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.WikiToStorageConverter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WikiToStorageConverterImpl
implements WikiToStorageConverter {
    private static final Logger log = LoggerFactory.getLogger(WikiToStorageConverterImpl.class);
    private final ExceptionTolerantMigrator wikiToXhtmlMigrator;

    public WikiToStorageConverterImpl(ExceptionTolerantMigrator wikiToXhtmlMigrator) {
        this.wikiToXhtmlMigrator = wikiToXhtmlMigrator;
    }

    @Override
    public String convertWikiToStorage(String wikiContent, ConversionContext context, List<RuntimeException> migrationExceptions) {
        return this.wikiToXhtmlMigrator.migrate(wikiContent, context != null ? context.getPageContext() : null, migrationExceptions);
    }

    @Override
    public <T extends ContentEntityObject> T convertWikiBodyToStorage(T ceo) {
        BodyContent bodyContent = ((ContentEntityObject)ceo).getBodyContent();
        if (bodyContent.getBodyType() == BodyType.WIKI) {
            ArrayList<RuntimeException> migrationExceptions = new ArrayList<RuntimeException>();
            String storageFormat = this.convertWikiToStorage(bodyContent.getBody(), new DefaultConversionContext(((ContentEntityObject)ceo).toPageContext()), migrationExceptions);
            if (!migrationExceptions.isEmpty() && log.isDebugEnabled()) {
                StringBuilder builder = new StringBuilder("There were ");
                builder.append(migrationExceptions.size()).append(" exceptions during migration of wiki to XHTML.\n");
                for (RuntimeException ex : migrationExceptions) {
                    builder.append(ex.toString()).append("\n");
                }
                log.debug(builder.toString());
            }
            ceo = (ContentEntityObject)((ContentEntityObject)ceo).clone();
            ((ContentEntityObject)ceo).setBodyContent(new BodyContent((ContentEntityObject)ceo, storageFormat, BodyType.XHTML));
        } else if (bodyContent.getBodyType() == BodyType.RAW) {
            throw new IllegalArgumentException("This method can not convert CEOs with BodyType of RAW.");
        }
        return (T)ceo;
    }
}

