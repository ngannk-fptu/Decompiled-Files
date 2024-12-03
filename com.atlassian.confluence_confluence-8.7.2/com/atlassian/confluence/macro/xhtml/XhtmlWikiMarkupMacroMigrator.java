/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.AbstractExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import java.util.ArrayList;

public class XhtmlWikiMarkupMacroMigrator
extends AbstractExceptionTolerantMigrator {
    private final XhtmlContent xhtmlContent;
    private final ExceptionTolerantMigrator delegateMigrator;

    public XhtmlWikiMarkupMacroMigrator(XhtmlContent xhtmlContent, ExceptionTolerantMigrator delegateMigrator) {
        this.xhtmlContent = xhtmlContent;
        this.delegateMigrator = delegateMigrator;
    }

    @Override
    public ExceptionTolerantMigrator.MigrationResult migrate(String inputXhtml, ConversionContext conversionContext) {
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        try {
            String migratedContent = this.xhtmlContent.replaceMacroDefinitionsWithString(inputXhtml, conversionContext, macroDefinition -> {
                if ("unmigrated-wiki-markup".equals(macroDefinition.getName()) || "unmigrated-inline-wiki-markup".equals(macroDefinition.getName())) {
                    ExceptionTolerantMigrator.MigrationResult delegateResult = this.delegateMigrator.migrate(macroDefinition.getBodyText(), conversionContext);
                    if (delegateResult.getExceptions().isEmpty()) {
                        return delegateResult.getContent();
                    }
                    exceptions.addAll(delegateResult.getExceptions());
                }
                return this.xhtmlContent.convertMacroDefinitionToStorage(macroDefinition, conversionContext);
            });
            return new ExceptionTolerantMigrator.MigrationResult(migratedContent, true, exceptions);
        }
        catch (XhtmlException e) {
            exceptions.add(new RuntimeException(e));
            return new ExceptionTolerantMigrator.MigrationResult(inputXhtml, false, exceptions);
        }
    }
}

