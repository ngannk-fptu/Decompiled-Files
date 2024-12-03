/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.model;

public class SamlResponseStatus {
    private String statusCode;
    private String subStatusCode;
    private String statusMessage;

    public SamlResponseStatus(String statusCode) {
        this.statusCode = statusCode;
    }

    public SamlResponseStatus(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSubStatusCode() {
        return this.subStatusCode;
    }

    public void setSubStatusCode(String subStatusCode) {
        this.subStatusCode = subStatusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean is(String status) {
        return this.statusCode != null && !this.statusCode.isEmpty() && this.statusCode.equals(status);
    }
}

