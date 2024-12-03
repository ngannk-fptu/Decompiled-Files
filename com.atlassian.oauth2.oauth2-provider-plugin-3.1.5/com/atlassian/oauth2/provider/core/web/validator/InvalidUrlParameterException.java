/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.core.web.validator;

public class InvalidUrlParameterException
extends RuntimeException {
    private String invalidParameterKey;

    public InvalidUrlParameterException(String invalidParameterKey) {
        this.invalidParameterKey = invalidParameterKey;
    }

    public String getUrlParameterKey() {
        return this.invalidParameterKey;
    }
}

