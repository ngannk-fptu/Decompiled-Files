/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class MigrateAwayWikiCompatibleRegexParameters
implements MacroMigration {
    private static Pattern COMMA_TO_PIPE_REPLACE_PATTERN = Pattern.compile("//|,\\s*");
    private static Pattern ESCAPED_COMMA_TO_COMMA_REPLACE_PATTERN = Pattern.compile("\\\\x2[c|C]");
    private MacroMigration richTextMacroMigration;

    public MigrateAwayWikiCompatibleRegexParameters(@Qualifier(value="richTextMacroMigration") MacroMigration richTextMacroMigration) {
        this.richTextMacroMigration = richTextMacroMigration;
    }

    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext context) {
        Map params = (macroDefinition = this.richTextMacroMigration.migrate(macroDefinition, context)).getParameters();
        String paramValue = (String)params.get("include");
        if (StringUtils.isNotBlank((CharSequence)paramValue)) {
            params.put("include", MigrateAwayWikiCompatibleRegexParameters.convertRegex(paramValue));
        }
        if (StringUtils.isNotBlank((CharSequence)(paramValue = (String)params.get("exclude")))) {
            params.put("exclude", MigrateAwayWikiCompatibleRegexParameters.convertRegex(paramValue));
        }
        macroDefinition.setParameters(params);
        return macroDefinition;
    }

    private static String convertRegex(String pattern) {
        pattern = COMMA_TO_PIPE_REPLACE_PATTERN.matcher(pattern).replaceAll("|");
        pattern = ESCAPED_COMMA_TO_COMMA_REPLACE_PATTERN.matcher(pattern).replaceAll(",");
        return pattern;
    }
}

