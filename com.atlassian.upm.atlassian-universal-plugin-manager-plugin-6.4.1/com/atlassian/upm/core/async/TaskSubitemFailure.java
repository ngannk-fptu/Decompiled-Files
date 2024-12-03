/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.async;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public final class TaskSubitemFailure {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String version;
    @JsonProperty
    private final String errorCode;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final String source;
    @JsonIgnore
    private final Type type;

    @JsonCreator
    public TaskSubitemFailure(@JsonProperty(value="type") String type, @JsonProperty(value="name") String name, @JsonProperty(value="key") String key, @JsonProperty(value="version") String version, @JsonProperty(value="errorCode") String errorCode, @JsonProperty(value="message") String message, @JsonProperty(value="source") String source) {
        this(type == null ? null : Type.valueOf(type), name, key, version, errorCode, message, source);
    }

    public TaskSubitemFailure(Type type, String name, String key, String version, String errorCode, String message, String source) {
        this.type = type;
        this.name = name;
        this.key = key;
        this.version = version;
        this.errorCode = errorCode;
        this.message = message;
        this.source = source;
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getVersion() {
        return this.version;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getSource() {
        return this.source;
    }

    @JsonProperty
    public Type getType() {
        return this.type;
    }

    public String toString() {
        return "TaskSubitemFailure(" + (Object)((Object)this.type) + ", " + this.name + ", " + this.key + ", " + this.version + ", " + this.errorCode + ", " + this.message + ")";
    }

    public static enum Type {
        DOWNLOAD,
        INSTALL,
        UNINSTALL;

    }
}

