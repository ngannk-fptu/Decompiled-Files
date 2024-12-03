/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.async;

import com.atlassian.upm.api.util.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AsyncTaskErrorInfo {
    @JsonProperty
    private final String code;
    @JsonProperty
    private final String message;

    @JsonCreator
    public AsyncTaskErrorInfo(@JsonProperty(value="code") String code, @JsonProperty(value="message") String message) {
        this.code = code;
        this.message = message;
    }

    @JsonIgnore
    public Option<String> getCode() {
        return Option.option(this.code);
    }

    @JsonIgnore
    public Option<String> getMessage() {
        return Option.option(this.message);
    }

    public String toString() {
        if (this.message != null) {
            return this.code == null ? this.message : this.code + ", " + this.message;
        }
        return this.code;
    }
}

