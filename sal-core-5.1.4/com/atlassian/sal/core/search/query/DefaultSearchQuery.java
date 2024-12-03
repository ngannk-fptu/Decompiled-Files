/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.search.parameter.SearchParameter
 *  com.atlassian.sal.api.search.query.SearchQuery
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sal.core.search.query;

import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.core.search.parameter.BasicSearchParameter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DefaultSearchQuery
implements SearchQuery {
    private StringBuilder searchString = new StringBuilder();
    private Map<String, SearchParameter> parameters = new LinkedHashMap<String, SearchParameter>();

    public DefaultSearchQuery(String query) {
        this.append(query);
    }

    public SearchQuery setParameter(String name, String value) {
        this.parameters.put(name, new BasicSearchParameter(name, value));
        return this;
    }

    public String getParameter(String name) {
        SearchParameter value = this.parameters.get(name);
        return value == null ? null : value.getValue();
    }

    public String buildQueryString() {
        StringBuilder builder = new StringBuilder(this.searchString);
        for (SearchParameter parameter : this.parameters.values()) {
            builder.append('&');
            builder.append(parameter.buildQueryString());
        }
        return builder.toString();
    }

    public SearchQuery append(String query) {
        if (StringUtils.isEmpty((CharSequence)query)) {
            throw new IllegalArgumentException("Cannot parse empty query string!");
        }
        if (!query.contains("&")) {
            this.searchString.append(query);
            return this;
        }
        String[] strings = query.split("&");
        this.searchString.append(strings[0]);
        for (int i = 1; i < strings.length; ++i) {
            String string = strings[i];
            BasicSearchParameter searchParam = new BasicSearchParameter(string);
            this.parameters.put(searchParam.getName(), searchParam);
        }
        return this;
    }

    public String getSearchString() {
        try {
            return URLDecoder.decode(this.searchString.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public int getParameter(String name, int defaultValue) {
        try {
            return Integer.parseInt(this.getParameter(name));
        }
        catch (NumberFormatException numberFormatException) {
            return defaultValue;
        }
    }
}

