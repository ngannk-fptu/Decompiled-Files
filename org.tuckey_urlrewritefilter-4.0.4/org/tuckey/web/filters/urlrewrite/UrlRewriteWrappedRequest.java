/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package org.tuckey.web.filters.urlrewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class UrlRewriteWrappedRequest
extends HttpServletRequestWrapper {
    HashMap overridenParameters;
    String overridenMethod;

    public UrlRewriteWrappedRequest(HttpServletRequest httpServletRequest) {
        super(httpServletRequest);
    }

    public UrlRewriteWrappedRequest(HttpServletRequest httpServletRequest, HashMap overridenParameters, String overridenMethod) {
        super(httpServletRequest);
        this.overridenParameters = overridenParameters;
        this.overridenMethod = overridenMethod;
    }

    public Enumeration getParameterNames() {
        if (this.overridenParameters != null) {
            ArrayList keys = Collections.list(super.getParameterNames());
            keys.addAll(this.overridenParameters.keySet());
            return Collections.enumeration(keys);
        }
        return super.getParameterNames();
    }

    public Map getParameterMap() {
        if (this.overridenParameters != null) {
            Map superMap = super.getParameterMap();
            HashMap overriddenMap = new HashMap(superMap.size() + this.overridenParameters.size());
            overriddenMap.putAll(superMap);
            overriddenMap.putAll(this.overridenParameters);
            return overriddenMap;
        }
        return super.getParameterMap();
    }

    public String[] getParameterValues(String s) {
        if (this.overridenParameters != null && this.overridenParameters.containsKey(s)) {
            return (String[])this.overridenParameters.get(s);
        }
        return super.getParameterValues(s);
    }

    public String getParameter(String s) {
        if (this.overridenParameters != null && this.overridenParameters.containsKey(s)) {
            String[] values = (String[])this.overridenParameters.get(s);
            if (values == null || values.length == 0) {
                return null;
            }
            return values[0];
        }
        return super.getParameter(s);
    }

    public String getMethod() {
        if (this.overridenMethod != null) {
            return this.overridenMethod;
        }
        return super.getMethod();
    }
}

