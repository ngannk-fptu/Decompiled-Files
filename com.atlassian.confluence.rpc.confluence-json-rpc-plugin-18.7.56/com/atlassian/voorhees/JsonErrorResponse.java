/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

import com.atlassian.voorhees.JsonError;

class JsonErrorResponse {
    private final Object id;
    private final JsonError error;

    JsonErrorResponse(Object id, JsonError error) {
        this.id = id;
        this.error = error;
    }

    public Object getId() {
        return this.id;
    }

    public JsonError getError() {
        return this.error;
    }

    public String getJsonrpc() {
        return "2.0";
    }
}

