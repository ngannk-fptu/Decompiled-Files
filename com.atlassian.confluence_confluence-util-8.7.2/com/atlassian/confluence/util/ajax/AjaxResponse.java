/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.ajax;

public class AjaxResponse {
    private final boolean success;
    private final String response;

    private AjaxResponse(boolean success, String response) {
        this.success = success;
        this.response = response;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getResponse() {
        return this.response;
    }

    public static AjaxResponse failure(String response) {
        return new AjaxResponse(false, response);
    }

    public static AjaxResponse success(String response) {
        return new AjaxResponse(true, response);
    }
}

