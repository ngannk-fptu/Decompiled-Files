/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite;

public class RewrittenOutboundUrl {
    private String target;
    private boolean encode;

    public RewrittenOutboundUrl(String target, boolean encode) {
        this.target = target;
        this.encode = encode;
    }

    public String getTarget() {
        return this.target;
    }

    public void setEncode(boolean b) {
        this.encode = b;
    }

    public boolean isEncode() {
        return this.encode;
    }

    public void setTarget(String s) {
        this.target = s;
    }
}

