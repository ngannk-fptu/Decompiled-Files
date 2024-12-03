/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.http;

import java.util.List;
import org.bedework.util.misc.Util;

public class RequestBuilder {
    final StringBuilder req = new StringBuilder();
    String delim = "?";

    public RequestBuilder(String path) {
        this.req.append(path);
    }

    public void par(String name, String value) {
        this.req.append(this.delim);
        this.delim = "&";
        this.req.append(name);
        this.req.append("=");
        this.req.append(value);
    }

    public void par(String name, int value) {
        this.par(name, String.valueOf(value));
    }

    public void par(String name, List<String> value) {
        if (Util.isEmpty(value)) {
            return;
        }
        this.req.append(this.delim);
        this.delim = "&";
        this.req.append(this.delim);
        this.req.append(name);
        this.req.append("=");
        this.req.append(String.join((CharSequence)",", value));
    }

    public String toString() {
        return this.req.toString();
    }
}

