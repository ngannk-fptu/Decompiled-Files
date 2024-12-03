/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.confluence.impl.logging.log4j.layout.Log4j2LegacyPatternLayoutWithStackTraceBuilder;
import java.util.Properties;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name="com.atlassian.confluence.util.PatternLayoutWithContext", category="Log4j Builder")
public final class Log4j2LegacyPatternLayoutWithContextBuilder
extends Log4j2LegacyPatternLayoutWithStackTraceBuilder {
    public Log4j2LegacyPatternLayoutWithContextBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    protected String modifyPattern(String pattern) {
        return pattern + "%loggingcontext%stacktrace";
    }
}

