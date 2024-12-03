/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.json;

class JsonRpcErrorBean {
    private String name;
    private String message;
    private Object error;

    JsonRpcErrorBean() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getError() {
        return this.error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}

