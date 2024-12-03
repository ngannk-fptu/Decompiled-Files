/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource.util;

public final class StringResource {
    private String body;
    private String encoding;
    private long lastModified;

    public StringResource(String body, String encoding) {
        this.setBody(body);
        this.setEncoding(encoding);
    }

    public String getBody() {
        return this.body;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void setBody(String body) {
        this.body = body;
        this.lastModified = System.currentTimeMillis();
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

