/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.config.plugins.PluginAliases
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.confluence.impl.logging.log4j.layout.Log4j2AbstractPatternModifyingLayoutBuilder;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;

@Plugin(name="com.atlassian.confluence.impl.logging.ConfluencePatternLayout", category="Log4j Builder")
@PluginAliases(value={"com.atlassian.logging.log4j.NewLineIndentingFilteringPatternLayout"})
public class Log4j2ConfluencePatternLayoutBuilder
extends Log4j2AbstractPatternModifyingLayoutBuilder {
    private static final String[] LEGACY_PATTERN_SPECIFIERS = new String[]{"%e", "%X"};
    private static final String[] REPLACEMENT_PATTERN_SPECIFIERS = new String[]{"%stacktrace", "%loggingcontext"};

    public Log4j2ConfluencePatternLayoutBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    protected String modifyPattern(String pattern) {
        return StringUtils.replaceEach((String)pattern, (String[])LEGACY_PATTERN_SPECIFIERS, (String[])REPLACEMENT_PATTERN_SPECIFIERS);
    }
}

