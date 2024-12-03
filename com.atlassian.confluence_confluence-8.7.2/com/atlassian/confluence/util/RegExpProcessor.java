/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegExpProcessor {
    private static final Logger log = LoggerFactory.getLogger(RegExpProcessor.class);
    private Pattern pattern;
    private String substitute;
    private RegExpProcessorHandler handler;

    public RegExpProcessor(String pattern, String substitute) {
        this(pattern, substitute, null);
    }

    public RegExpProcessor(String pattern, String substitute, RegExpProcessorHandler handler) {
        this(pattern, substitute, true, handler);
    }

    public RegExpProcessor(String pattern, String substitute, boolean multiline, RegExpProcessorHandler handler) {
        this.pattern = Pattern.compile(pattern, 0x20 | (multiline ? 8 : 0));
        this.substitute = substitute;
        this.handler = handler;
    }

    public String getPattern() {
        return this.pattern.pattern();
    }

    public String getSubstitute() {
        return this.substitute;
    }

    public void setSubstitute(String substitute) {
        this.substitute = substitute;
    }

    public RegExpProcessorHandler getHandler() {
        return this.handler;
    }

    public void setHandler(RegExpProcessorHandler handler) {
        this.handler = handler;
    }

    public String process(String input) {
        return this.process(input, this.handler);
    }

    public String process(String input, RegExpProcessorHandler handler) {
        if (input == null) {
            return null;
        }
        Matcher matcher = this.pattern.matcher(input);
        if (handler == null) {
            return matcher.replaceAll(this.substitute);
        }
        StringBuffer result = new StringBuffer();
        try {
            while (matcher.find()) {
                matcher.appendReplacement(result, StringUtils.defaultString((String)this.substitute));
                handler.handleMatch(result, matcher, this);
            }
        }
        catch (StackOverflowError e) {
            log.error("Stack overflow error while processing regex: " + input + " with handler " + handler);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public String toString() {
        return "Pattern: [" + this.getPattern() + "]  Substitute: [" + this.getSubstitute() + "]";
    }

    public static interface RegExpProcessorHandler {
        public void handleMatch(StringBuffer var1, Matcher var2, RegExpProcessor var3);
    }
}

