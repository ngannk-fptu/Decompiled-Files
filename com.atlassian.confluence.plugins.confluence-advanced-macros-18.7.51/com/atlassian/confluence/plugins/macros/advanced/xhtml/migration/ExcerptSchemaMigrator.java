/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptType;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.apache.commons.lang3.StringUtils;

public class ExcerptSchemaMigrator
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
        String oldValue = macro.getParameter("excerpt");
        if (StringUtils.isNotBlank((CharSequence)oldValue)) {
            ExcerptType excerptParam = ExcerptType.fromOldValue(oldValue);
            macro.setParameter("excerptType", excerptParam.getValue());
            macro.setTypedParameter("excerptType", (Object)excerptParam);
        }
        macro.setSchemaVersion(macro.getSchemaVersion() + 1);
        return macro;
    }
}

