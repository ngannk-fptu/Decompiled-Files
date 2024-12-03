/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.onelogin.saml2.http;

import com.onelogin.saml2.util.Preconditions;
import com.onelogin.saml2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public final class HttpRequest {
    public static final Map<String, List<String>> EMPTY_PARAMETERS = Collections.emptyMap();
    private final String requestURL;
    private final Map<String, List<String>> parameters;
    private final String queryString;

    @Deprecated
    public HttpRequest(String requestURL) {
        this(requestURL, EMPTY_PARAMETERS);
    }

    public HttpRequest(String requestURL, String queryString) {
        this(requestURL, EMPTY_PARAMETERS, queryString);
    }

    @Deprecated
    public HttpRequest(String requestURL, Map<String, List<String>> parameters) {
        this(requestURL, parameters, null);
    }

    public HttpRequest(String requestURL, Map<String, List<String>> parameters, String queryString) {
        this.requestURL = Preconditions.checkNotNull(requestURL, "requestURL");
        this.parameters = HttpRequest.unmodifiableCopyOf(Preconditions.checkNotNull(parameters, "queryParams"));
        this.queryString = StringUtils.trimToEmpty((String)queryString);
    }

    public HttpRequest addParameter(String name, String value) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(value, "value");
        ArrayList oldValues = this.parameters.containsKey(name) ? this.parameters.get(name) : new ArrayList();
        ArrayList<String> newValues = new ArrayList<String>(oldValues);
        newValues.add(value);
        HashMap<String, List<String>> params = new HashMap<String, List<String>>(this.parameters);
        params.put(name, newValues);
        return new HttpRequest(this.requestURL, params, this.queryString);
    }

    public HttpRequest removeParameter(String name) {
        Preconditions.checkNotNull(name, "name");
        HashMap<String, List<String>> params = new HashMap<String, List<String>>(this.parameters);
        params.remove(name);
        return new HttpRequest(this.requestURL, params, this.queryString);
    }

    public String getRequestURL() {
        return this.requestURL;
    }

    public String getParameter(String name) {
        List<String> values = this.getParameters(name);
        return values.isEmpty() ? null : values.get(0);
    }

    public List<String> getParameters(String name) {
        List<String> values = this.parameters.get(name);
        return values != null ? values : Collections.emptyList();
    }

    public Map<String, List<String>> getParameters() {
        return this.parameters;
    }

    public String getEncodedParameter(String name) {
        Matcher matcher = Pattern.compile(Pattern.quote(name) + "=([^&#]+)").matcher(this.queryString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return Util.urlEncoder(this.getParameter(name));
    }

    public String getEncodedParameter(String name, String defaultValue) {
        String value = this.getEncodedParameter(name);
        return value != null ? value : Util.urlEncoder(defaultValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest)o;
        return Objects.equals(this.requestURL, that.requestURL) && Objects.equals(this.parameters, that.parameters) && Objects.equals(this.queryString, that.queryString);
    }

    public int hashCode() {
        return Objects.hash(this.requestURL, this.parameters, this.queryString);
    }

    public String toString() {
        return "HttpRequest{requestURL='" + this.requestURL + '\'' + ", parameters=" + this.parameters + ", queryString=" + this.queryString + '}';
    }

    private static Map<String, List<String>> unmodifiableCopyOf(Map<String, List<String>> orig) {
        HashMap copy = new HashMap();
        for (Map.Entry<String, List<String>> entry : orig.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(new ArrayList(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}

