/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

public enum SoapBodyStyle {
    DocumentBare,
    DocumentWrapper,
    RpcLiteral,
    RpcEncoded,
    Unspecificed;


    public boolean isDocument() {
        return this.equals((Object)DocumentBare) || this.equals((Object)DocumentWrapper);
    }

    public boolean isRpc() {
        return this.equals((Object)RpcLiteral) || this.equals((Object)RpcEncoded);
    }

    public boolean isLiteral() {
        return this.equals((Object)RpcLiteral) || this.isDocument();
    }

    public boolean isBare() {
        return this.equals((Object)DocumentBare);
    }

    public boolean isDocumentWrapper() {
        return this.equals((Object)DocumentWrapper);
    }
}

