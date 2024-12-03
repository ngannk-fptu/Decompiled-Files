/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

class JsonSuccessResponse {
    private Object id;
    private Object result;

    public JsonSuccessResponse(Object id, Object result) {
        this.id = id;
        this.result = result;
    }

    public Object getId() {
        return this.id;
    }

    public Object getResult() {
        return this.result;
    }

    public String getJsonrpc() {
        return "2.0";
    }
}

