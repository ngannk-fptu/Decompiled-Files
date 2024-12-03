/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import io.atlassian.fugue.Option;
import java.util.Iterator;

public class ErrorDetail {
    String message;
    Option<String> code;
    Option<String> path;

    public String getMessage() {
        return this.message;
    }

    public Option<String> getCode() {
        return this.code;
    }

    public Option<String> getPath() {
        return this.path;
    }

    public String toString() {
        Iterator iterator = this.path.iterator();
        if (iterator.hasNext()) {
            String p = (String)iterator.next();
            return p + ": " + this.message;
        }
        return this.message;
    }

    public boolean equals(Object other) {
        if (other instanceof ErrorDetail) {
            ErrorDetail o = (ErrorDetail)other;
            return this.message.equals(o.message) && this.code.equals(o.code) && this.path.equals(o.path);
        }
        return false;
    }

    public int hashCode() {
        return this.message.hashCode() + this.code.hashCode() + this.path.hashCode();
    }
}

