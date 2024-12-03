/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.applinks.ui.velocity;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class OAuth2Result {
    private static final Set<String> ACCEPTED_TYPES = ImmutableSet.of((Object)"info", (Object)"error", (Object)"warning", (Object)"success");
    private final String type;
    private final String message;

    OAuth2Result(String type, String message) {
        this.type = this.validateType(type);
        this.message = message;
    }

    private String validateType(String type) {
        if (!ACCEPTED_TYPES.contains(type)) {
            throw new IllegalArgumentException("type [" + type + "] must be one of " + ACCEPTED_TYPES);
        }
        return type;
    }

    static OAuth2Result empty() {
        return new OAuth2Result();
    }

    private OAuth2Result() {
        this.type = null;
        this.message = null;
    }

    public String getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isEmpty() {
        return this.type == null;
    }
}

