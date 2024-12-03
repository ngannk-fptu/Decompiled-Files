/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 */
package io.micrometer.core.instrument.binder.http;

import io.micrometer.common.KeyValue;
import io.micrometer.core.instrument.Tag;

public enum Outcome {
    INFORMATIONAL,
    SUCCESS,
    REDIRECTION,
    CLIENT_ERROR,
    SERVER_ERROR,
    UNKNOWN;

    private final Tag tag = Tag.of("outcome", this.name());
    private final KeyValue keyValue = KeyValue.of((String)"outcome", (String)this.name());

    public Tag asTag() {
        return this.tag;
    }

    public KeyValue asKeyValue() {
        return this.keyValue;
    }

    public static Outcome forStatus(int status) {
        if (status >= 100 && status < 200) {
            return INFORMATIONAL;
        }
        if (status >= 200 && status < 300) {
            return SUCCESS;
        }
        if (status >= 300 && status < 400) {
            return REDIRECTION;
        }
        if (status >= 400 && status < 500) {
            return CLIENT_ERROR;
        }
        if (status >= 500 && status < 600) {
            return SERVER_ERROR;
        }
        return UNKNOWN;
    }
}

