/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.message;

import com.sun.xml.ws.encoding.ContentTypeImpl;

public interface ContentType {
    public String getContentType();

    public String getSOAPActionHeader();

    public String getAcceptHeader();

    public static class Builder {
        private String contentType;
        private String soapAction;
        private String accept;
        private String charset;

        public Builder contentType(String s) {
            this.contentType = s;
            return this;
        }

        public Builder soapAction(String s) {
            this.soapAction = s;
            return this;
        }

        public Builder accept(String s) {
            this.accept = s;
            return this;
        }

        public Builder charset(String s) {
            this.charset = s;
            return this;
        }

        public ContentType build() {
            return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset);
        }
    }
}

