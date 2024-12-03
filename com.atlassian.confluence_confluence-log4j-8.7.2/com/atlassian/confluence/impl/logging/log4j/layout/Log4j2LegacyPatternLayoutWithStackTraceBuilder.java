/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.confluence.impl.logging.log4j.layout.Log4j2AbstractPatternModifyingLayoutBuilder;
import java.util.Properties;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name="com.atlassian.confluence.util.PatternLayoutWithStackTrace", category="Log4j Builder")
public class Log4j2LegacyPatternLayoutWithStackTraceBuilder
extends Log4j2AbstractPatternModifyingLayoutBuilder {
    public Log4j2LegacyPatternLayoutWithStackTraceBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    protected String modifyPattern(String pattern) {
        return pattern + "%stacktrace";
    }
}

