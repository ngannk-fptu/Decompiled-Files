/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.json;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;
import org.tuckey.web.filters.urlrewrite.json.JsonRpcBean;
import org.tuckey.web.filters.urlrewrite.json.JsonRpcErrorBean;
import org.tuckey.web.filters.urlrewrite.json.JsonWriter;

public class JsonRewriteMatch
extends RewriteMatch {
    private Object returned;
    private Throwable throwable;

    public JsonRewriteMatch(Object returned) {
        this.returned = returned;
    }

    public JsonRewriteMatch(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Content-Type", "application/json");
        String jsonString = this.toJSONString(this.returned, this.throwable);
        response.setContentLength(jsonString.length());
        response.getOutputStream().write(jsonString.getBytes());
        return true;
    }

    public String toJSONString(Object resultantObject, Throwable resultantThrowable) {
        JsonWriter writer = new JsonWriter();
        JsonRpcBean bean = new JsonRpcBean();
        bean.setResult(resultantObject);
        if (resultantThrowable != null) {
            JsonRpcErrorBean error = new JsonRpcErrorBean();
            error.setName(resultantThrowable.getClass().getName());
            error.setMessage(resultantThrowable.getMessage());
            error.setError(resultantThrowable.toString());
            bean.setError(error);
        }
        return writer.write(bean);
    }
}

