/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.RegExUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.confluence.impl.logging.log4j.layout.Log4j2ConfluencePatternLayoutBuilder;
import java.util.Properties;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name="com.atlassian.confluence.impl.logging.ConfluenceControlCharsEscapedPatternLayout", category="Log4j Builder")
public final class Log4j2ConfluenceControlCharsEscapedPatternLayoutBuilder
extends Log4j2ConfluencePatternLayoutBuilder {
    private static final String MESSAGE_CONVERSION_PATTERN = "%m";
    private static final String ESCAPED_JSON_MESSAGE_CONVERSION_PATTERN = "%enc{%m}{JSON}";
    private static final String ESCAPED_MESSAGE_CONVERSION_PATTERN_REGEX = "%enc\\{%m\\}(?:\\{(?:HTML|XML|JSON|CRLF)\\})?";

    public Log4j2ConfluenceControlCharsEscapedPatternLayoutBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    protected String modifyPattern(String pattern) {
        String tempPattern = RegExUtils.replacePattern((String)pattern, (String)ESCAPED_MESSAGE_CONVERSION_PATTERN_REGEX, (String)MESSAGE_CONVERSION_PATTERN);
        return StringUtils.replace((String)super.modifyPattern(tempPattern), (String)MESSAGE_CONVERSION_PATTERN, (String)ESCAPED_JSON_MESSAGE_CONVERSION_PATTERN);
    }
}

