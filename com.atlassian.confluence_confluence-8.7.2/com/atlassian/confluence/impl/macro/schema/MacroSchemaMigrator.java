/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.macro.schema;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface MacroSchemaMigrator {
    public MacroDefinition migrateSchemaIfNecessary(MacroDefinition var1, ConversionContext var2) throws XhtmlException;
}

