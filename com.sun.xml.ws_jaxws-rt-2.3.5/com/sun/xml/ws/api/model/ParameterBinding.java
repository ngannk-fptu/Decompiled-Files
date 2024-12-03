/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model;

public final class ParameterBinding {
    public static final ParameterBinding BODY = new ParameterBinding(Kind.BODY, null);
    public static final ParameterBinding HEADER = new ParameterBinding(Kind.HEADER, null);
    public static final ParameterBinding UNBOUND = new ParameterBinding(Kind.UNBOUND, null);
    public final Kind kind;
    private String mimeType;

    public static ParameterBinding createAttachment(String mimeType) {
        return new ParameterBinding(Kind.ATTACHMENT, mimeType);
    }

    private ParameterBinding(Kind kind, String mimeType) {
        this.kind = kind;
        this.mimeType = mimeType;
    }

    public String toString() {
        return this.kind.toString();
    }

    public String getMimeType() {
        if (!this.isAttachment()) {
            throw new IllegalStateException();
        }
        return this.mimeType;
    }

    public boolean isBody() {
        return this == BODY;
    }

    public boolean isHeader() {
        return this == HEADER;
    }

    public boolean isUnbound() {
        return this == UNBOUND;
    }

    public boolean isAttachment() {
        return this.kind == Kind.ATTACHMENT;
    }

    public static enum Kind {
        BODY,
        HEADER,
        UNBOUND,
        ATTACHMENT;

    }
}

