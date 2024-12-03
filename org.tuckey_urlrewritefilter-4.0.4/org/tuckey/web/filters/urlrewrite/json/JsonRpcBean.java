/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.json;

import org.tuckey.web.filters.urlrewrite.json.JsonRpcErrorBean;

class JsonRpcBean {
    private Object result;
    private JsonRpcErrorBean error;

    JsonRpcBean() {
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public JsonRpcErrorBean getError() {
        return this.error;
    }

    public void setError(JsonRpcErrorBean error) {
        this.error = error;
    }
}

