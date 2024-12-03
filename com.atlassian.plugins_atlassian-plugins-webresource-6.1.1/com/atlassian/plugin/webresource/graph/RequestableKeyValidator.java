/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.graph;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestableKeyValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestableKeyValidator.class);
    private static final String WEB_RESOURCE_SYMBOL = ":";
    private final Set<String> rootPageKeys;

    public RequestableKeyValidator(@Nonnull Set<String> rootPageKeys) {
        this.rootPageKeys = Objects.requireNonNull(rootPageKeys, "The root page keys are mandatory.");
    }

    private static boolean isWebResourceKey(@Nonnull String requestableKey) {
        return requestableKey.contains(WEB_RESOURCE_SYMBOL);
    }

    public static boolean isWebResourceContext(@Nonnull String requestableDependencyKey) {
        Objects.requireNonNull(requestableDependencyKey, "The requestable dependency key is mandatory.");
        if (RequestableKeyValidator.isWebResourceKey(requestableDependencyKey)) {
            String message = String.format("Requestable key '%s' looks like a web resource.", requestableDependencyKey);
            LOGGER.debug(message);
            return false;
        }
        return true;
    }

    public boolean isWebResource(@Nonnull String requestableDependencyKey) {
        Objects.requireNonNull(requestableDependencyKey, "The web resource key is mandatory.");
        if (this.rootPageKeys.contains(requestableDependencyKey)) {
            String message = String.format("Requestable key '%s' is tagged as a root-page.", requestableDependencyKey);
            LOGGER.debug(message);
            return false;
        }
        return RequestableKeyValidator.isWebResourceKey(requestableDependencyKey);
    }
}

