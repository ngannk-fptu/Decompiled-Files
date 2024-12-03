/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.plugins.mobile.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;

public class Context {
    private Type type;
    private String spaceKey;
    private Long contentId;

    public Context(Type type, String spaceKey, Long contentId) {
        this.type = type;
        this.spaceKey = spaceKey;
        this.contentId = contentId;
    }

    public Type getType() {
        return this.type;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public static enum Type {
        GLOBAL("global"),
        SPACE("space"),
        PAGE("page"),
        BLOGPOST("blogpost");

        private static Map<String, Type> contextMap;
        private String value;

        private Type(String value) {
            this.value = value;
        }

        @JsonCreator
        public static Type forValue(String value) {
            return contextMap.get(value);
        }

        static {
            contextMap = ImmutableMap.builder().put((Object)"global", (Object)GLOBAL).put((Object)"space", (Object)SPACE).put((Object)"page", (Object)PAGE).put((Object)"blogpost", (Object)BLOGPOST).build();
        }
    }
}

