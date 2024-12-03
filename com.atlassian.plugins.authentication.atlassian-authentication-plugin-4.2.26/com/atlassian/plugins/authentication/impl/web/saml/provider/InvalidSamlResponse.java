/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.saml.provider;

import com.atlassian.plugins.authentication.impl.web.usercontext.IdentifiableRuntimeException;

public class InvalidSamlResponse
extends IdentifiableRuntimeException {
    private String targetUrl;
    private Long idpConfigId;

    public InvalidSamlResponse(Exception e) {
        super(e);
    }

    public InvalidSamlResponse(String message) {
        super(message);
    }

    public InvalidSamlResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public String getTargetUrl() {
        return this.targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Long getIdpConfigId() {
        return this.idpConfigId;
    }

    public void setIdpConfigId(Long idpConfigId) {
        this.idpConfigId = idpConfigId;
    }
}

