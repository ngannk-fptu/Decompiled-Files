/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package com.atlassian.core.filters.legacy;

import com.atlassian.core.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class WordCurlyQuotesRequestWrapper
extends HttpServletRequestWrapper {
    private final String encoding;
    private final Map<String, String[]> parameterValueCache = new HashMap<String, String[]>();
    private Map<String, String[]> parameterMap = null;

    public WordCurlyQuotesRequestWrapper(HttpServletRequest servletRequest, String encoding) {
        super(servletRequest);
        this.encoding = encoding;
    }

    public final String getParameter(String string) {
        return this.escapeString(super.getParameter(string));
    }

    private String escapeString(String string) {
        return StringUtils.escapeCP1252(string, this.encoding);
    }

    public final Map getParameterMap() {
        if (this.parameterMap == null) {
            Map original = super.getParameterMap();
            this.parameterMap = new HashMap<String, String[]>();
            for (String key : original.keySet()) {
                this.parameterMap.put(key, this.getParameterValues(key));
            }
        }
        return this.parameterMap;
    }

    public final String[] getParameterValues(String string) {
        String[] returnValue = this.parameterValueCache.get(string);
        if (returnValue == null) {
            String[] parameterValues = super.getParameterValues(string);
            if (parameterValues == null) {
                return null;
            }
            for (int i = 0; i < parameterValues.length; ++i) {
                String parameterValue;
                parameterValues[i] = parameterValue = this.escapeString(parameterValues[i]);
            }
            this.parameterValueCache.put(string, parameterValues);
            returnValue = parameterValues;
        }
        return returnValue;
    }
}

