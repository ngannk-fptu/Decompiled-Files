/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package com.atlassian.streams.internal.servlet;

import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

final class XsrfAwareRequest
extends HttpServletRequestWrapper {
    public static final String CROSS_PRODUCT_TOKEN_PARAM = "xsrfToken";
    private final String shadowParamName;

    XsrfAwareRequest(HttpServletRequest req, String shadowParamName) {
        super(req);
        this.shadowParamName = (String)Preconditions.checkNotNull((Object)shadowParamName);
    }

    public String getParameter(String name) {
        Object NULL_STRING = null;
        return name.equals(this.shadowParamName) && !this.shadowProvided() ? (String)this.xsrfToken().getOrElse(NULL_STRING) : super.getParameter(name);
    }

    public String[] getParameterValues(String name) {
        return name.equals(this.shadowParamName) && !this.shadowProvided() ? this.xsrfTokenAsStringArray() : super.getParameterValues(name);
    }

    public Enumeration<String> getParameterNames() {
        ArrayList nameList = Collections.list(super.getParameterNames());
        nameList.add(this.shadowParamName);
        return Collections.enumeration(nameList);
    }

    public Map<String, String[]> getParameterMap() {
        Map paramMap = super.getParameterMap();
        if (this.shadowProvided()) {
            return paramMap;
        }
        HashMap<String, String[]> enhancedParamMap = new HashMap<String, String[]>(paramMap);
        enhancedParamMap.put(this.shadowParamName, (String[])paramMap.get(CROSS_PRODUCT_TOKEN_PARAM));
        return enhancedParamMap;
    }

    private boolean shadowProvided() {
        return this.getRequest().getParameter(this.shadowParamName) != null;
    }

    private Option<String> xsrfToken() {
        return Option.option((Object)this.getParameter(CROSS_PRODUCT_TOKEN_PARAM));
    }

    private String[] xsrfTokenAsStringArray() {
        Function<String, String[]> stringToStringArray = new Function<String, String[]>(){

            public String[] apply(@Nullable String from) {
                return new String[]{from};
            }
        };
        Object NULL_STRINGS = null;
        return (String[])this.xsrfToken().map((Function)stringToStringArray).getOrElse(NULL_STRINGS);
    }
}

