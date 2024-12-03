/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public final class URLUtils {
    public static final String CHARSET = "utf-8";

    public static URL getBaseURL(URL url) {
        if (url == null) {
            return null;
        }
        try {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    public static String serializeParameters(Map<String, List<String>> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            for (String value : entry.getValue()) {
                if (value == null) {
                    value = "";
                }
                try {
                    String encodedKey = URLEncoder.encode(entry.getKey(), CHARSET);
                    String encodedValue = URLEncoder.encode(value, CHARSET);
                    if (sb.length() > 0) {
                        sb.append('&');
                    }
                    sb.append(encodedKey);
                    sb.append('=');
                    sb.append(encodedValue);
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return sb.toString();
    }

    public static String serializeParametersAlt(Map<String, String[]> params) {
        if (params == null) {
            return URLUtils.serializeParameters(null);
        }
        HashMap<String, List<String>> out = new HashMap<String, List<String>>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                out.put(entry.getKey(), null);
                continue;
            }
            out.put(entry.getKey(), Arrays.asList((Object[])entry.getValue()));
        }
        return URLUtils.serializeParameters(out);
    }

    public static Map<String, List<String>> parseParameters(String query) {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        if (StringUtils.isBlank(query)) {
            return params;
        }
        try {
            StringTokenizer st = new StringTokenizer(query.trim(), "&");
            while (st.hasMoreTokens()) {
                String value;
                String param = st.nextToken();
                String[] pair = param.split("=", 2);
                String key = URLDecoder.decode(pair[0], CHARSET);
                String string = value = pair.length > 1 ? URLDecoder.decode(pair[1], CHARSET) : "";
                if (params.containsKey(key)) {
                    LinkedList<String> updatedValueList = new LinkedList<String>((Collection)params.get(key));
                    updatedValueList.add(value);
                    params.put(key, Collections.unmodifiableList(updatedValueList));
                    continue;
                }
                params.put(key, Collections.singletonList(value));
            }
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        return params;
    }

    private URLUtils() {
    }
}

