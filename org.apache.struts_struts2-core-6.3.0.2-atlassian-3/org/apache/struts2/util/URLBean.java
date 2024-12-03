/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.apache.struts2.views.util.UrlHelper;

public class URLBean {
    HashMap<String, String> params;
    HttpServletRequest request;
    HttpServletResponse response;
    String page;
    private UrlHelper urlHelper;

    public URLBean setPage(String page) {
        this.page = page;
        return this;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        this.urlHelper = ActionContext.getContext().getInstance(DefaultUrlHelper.class);
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getURL() {
        HashMap<String, Object> fullParams = new HashMap<String, Object>();
        if (this.page == null) {
            fullParams.putAll(this.request.getParameterMap());
        }
        if (this.params != null) {
            fullParams.putAll(this.params);
        }
        return this.urlHelper.buildUrl(this.page, this.request, this.response, fullParams);
    }

    public URLBean addParameter(String name, Object value) {
        if (this.params == null) {
            this.params = new HashMap();
        }
        if (value == null) {
            this.params.remove(name);
        } else {
            this.params.put(name, value.toString());
        }
        return this;
    }

    public String toString() {
        return this.getURL();
    }
}

