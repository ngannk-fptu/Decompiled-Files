/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionThrowingMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.renderer.RenderContext;
import java.io.StringReader;
import org.apache.commons.lang3.StringUtils;

public class WikiToEditorHtmlMigrator
implements ExceptionThrowingMigrator {
    private final ExceptionTolerantMigrator wikiToStorageMigrator;
    private final Transformer storageToEditorTransformer;

    private WikiToEditorHtmlMigrator(ExceptionTolerantMigrator wikiToStorageMigrator, Transformer storeToEditorTransformer) {
        this.wikiToStorageMigrator = wikiToStorageMigrator;
        this.storageToEditorTransformer = storeToEditorTransformer;
    }

    @Override
    public String migrate(String wiki, RenderContext renderContext) throws XhtmlException {
        String storageFormat = this.wikiToStorageMigrator.migrate(wiki, renderContext, null);
        if (StringUtils.isBlank((CharSequence)storageFormat)) {
            return "";
        }
        return this.storageToEditorTransformer.transform(new StringReader(storageFormat), new DefaultConversionContext(renderContext));
    }
}

