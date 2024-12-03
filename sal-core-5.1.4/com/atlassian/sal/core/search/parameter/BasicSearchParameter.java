/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.search.parameter.SearchParameter
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sal.core.search.parameter;

import com.atlassian.sal.api.search.parameter.SearchParameter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;

public class BasicSearchParameter
implements SearchParameter {
    private String name;
    private String value;

    public BasicSearchParameter(String queryString) {
        this.initFromQueryString(queryString);
    }

    public BasicSearchParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String buildQueryString() {
        String encodedValue;
        String encodedName;
        try {
            encodedName = URLEncoder.encode(this.name, "UTF-8");
            encodedValue = URLEncoder.encode(this.value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unable to encode query parameters in UTF-8", e);
        }
        return encodedName + "=" + encodedValue;
    }

    private void initFromQueryString(String queryString) {
        if (StringUtils.isEmpty((CharSequence)queryString) || !queryString.contains("=")) {
            throw new IllegalArgumentException("QueryString '" + queryString + "' does not appear to be a valid query string");
        }
        try {
            String[] encodedQueryKeyValuePair = queryString.split("=");
            this.name = URLDecoder.decode(encodedQueryKeyValuePair[0], "UTF-8");
            this.value = URLDecoder.decode(encodedQueryKeyValuePair[1], "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BasicSearchParameter that = (BasicSearchParameter)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return this.value.equals(that.value);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }
}

