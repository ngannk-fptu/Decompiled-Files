/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidationError {
    private final String messageKey;
    private final List args;

    public ValidationError(String messageKey, Object ... args) {
        if (messageKey == null) {
            throw new IllegalArgumentException("Validation error message key can not be null");
        }
        this.messageKey = messageKey;
        this.args = args == null || args.length == 0 ? Collections.EMPTY_LIST : new ArrayList<Object>(Arrays.asList(args));
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public Object[] getArgs() {
        return this.args.toArray();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValidationError that = (ValidationError)o;
        if (!this.args.equals(that.args)) {
            return false;
        }
        return this.messageKey.equals(that.messageKey);
    }

    public int hashCode() {
        int result = this.messageKey.hashCode();
        result = 31 * result + this.args.hashCode();
        return result;
    }

    public String toString() {
        return "ValidationError [messageKey=" + this.messageKey + ", args=" + this.args + "]";
    }
}

