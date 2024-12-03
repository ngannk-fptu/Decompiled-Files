/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.gadgets.migrator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.apache.commons.lang3.StringUtils;

public class GadgetMacroMigrator
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        String macroBody = macroDefinition.getBodyText();
        if (StringUtils.isNotBlank((CharSequence)macroBody)) {
            macroDefinition.setParameter("preferences", macroBody);
            macroDefinition.setTypedParameter("preferences", (Object)macroBody);
        }
        return macroDefinition;
    }
}

