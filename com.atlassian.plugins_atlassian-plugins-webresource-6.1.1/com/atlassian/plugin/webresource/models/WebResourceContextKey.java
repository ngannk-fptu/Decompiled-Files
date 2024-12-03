/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.plugin.webresource.models.Requestable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebResourceContextKey
extends Requestable {
    private static final Pattern EXTRANEOUS_CONTEXT_PATTERN = Pattern.compile("^(?:_context[:]?)+");
    private static final Logger LOGGER = LoggerFactory.getLogger(WebResourceContextKey.class);

    public WebResourceContextKey(@Nonnull String key) {
        super(WebResourceContextKey.constructKey(key));
    }

    private static String constructKey(@Nonnull String key) {
        Matcher matcher = EXTRANEOUS_CONTEXT_PATTERN.matcher(key);
        if (matcher.find()) {
            LOGGER.debug("Provided key '{}' already contains '{}' prefix.", (Object)key, (Object)"_context");
            matcher.reset();
            return matcher.replaceAll("");
        }
        return key;
    }

    @Override
    @Deprecated
    public String toLooseType() {
        return String.format("%s:%s", "_context", this.getKey());
    }

    public String toString() {
        return String.format("<wrc!%s>", this.getKey());
    }
}

