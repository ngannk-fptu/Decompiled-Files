/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.renderer.RenderContext;
import java.util.List;

public abstract class AbstractExceptionTolerantMigrator
implements ExceptionTolerantMigrator {
    @Override
    public String migrate(String content, RenderContext renderContext, List<RuntimeException> exceptions) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(renderContext);
        ExceptionTolerantMigrator.MigrationResult migrationResult = this.migrate(content, conversionContext);
        exceptions.addAll(migrationResult.getExceptions());
        return migrationResult.getContent();
    }
}

