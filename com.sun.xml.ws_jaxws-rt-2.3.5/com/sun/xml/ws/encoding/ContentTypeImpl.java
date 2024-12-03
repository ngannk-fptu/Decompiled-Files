/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.encoding;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.encoding.ContentType;

public final class ContentTypeImpl
implements com.sun.xml.ws.api.pipe.ContentType {
    @NotNull
    private final String contentType;
    @NotNull
    private final String soapAction;
    private String accept;
    @Nullable
    private final String charset;
    private String boundary;
    private String boundaryParameter;
    private String rootId;
    private ContentType internalContentType;

    public ContentTypeImpl(String contentType) {
        this(contentType, null, null);
    }

    public ContentTypeImpl(String contentType, @Nullable String soapAction) {
        this(contentType, soapAction, null);
    }

    public ContentTypeImpl(String contentType, @Nullable String soapAction, @Nullable String accept) {
        this(contentType, soapAction, accept, null);
    }

    public ContentTypeImpl(String contentType, @Nullable String soapAction, @Nullable String accept, String charsetParam) {
        this.contentType = contentType;
        this.accept = accept;
        this.soapAction = this.getQuotedSOAPAction(soapAction);
        if (charsetParam == null) {
            String tmpCharset = null;
            try {
                this.internalContentType = new ContentType(contentType);
                tmpCharset = this.internalContentType.getParameter("charset");
                this.rootId = this.internalContentType.getParameter("start");
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.charset = tmpCharset;
        } else {
            this.charset = charsetParam;
        }
    }

    @Nullable
    public String getCharSet() {
        return this.charset;
    }

    private String getQuotedSOAPAction(String soapAction) {
        if (soapAction == null || soapAction.length() == 0) {
            return "\"\"";
        }
        if (soapAction.charAt(0) != '\"' && soapAction.charAt(soapAction.length() - 1) != '\"') {
            return "\"" + soapAction + "\"";
        }
        return soapAction;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getSOAPActionHeader() {
        return this.soapAction;
    }

    @Override
    public String getAcceptHeader() {
        return this.accept;
    }

    public void setAcceptHeader(String accept) {
        this.accept = accept;
    }

    public String getBoundary() {
        if (this.boundary == null) {
            if (this.internalContentType == null) {
                this.internalContentType = new ContentType(this.contentType);
            }
            this.boundary = this.internalContentType.getParameter("boundary");
        }
        return this.boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public String getBoundaryParameter() {
        return this.boundaryParameter;
    }

    public void setBoundaryParameter(String boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }

    public String getRootId() {
        if (this.rootId == null) {
            if (this.internalContentType == null) {
                this.internalContentType = new ContentType(this.contentType);
            }
            this.rootId = this.internalContentType.getParameter("start");
        }
        return this.rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public static class Builder {
        public String contentType;
        public String soapAction;
        public String accept;
        public String charset;

        public ContentTypeImpl build() {
            return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset);
        }
    }
}

