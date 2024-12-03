/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class QueryStringUtil {
    @Deprecated
    public static Map<String, String> toMap(String queryString) {
        return QueryStringUtil.toMap(queryString, GeneralUtil.getDefaultCharset());
    }

    public static Map<String, String> toMap(String queryString, Charset encoding) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (StringUtils.isNotBlank((CharSequence)queryString)) {
            for (String pair : queryString.split("&")) {
                String[] pairSplit = pair.split("=");
                if (pairSplit.length <= 0) continue;
                params.put(HtmlUtil.urlDecode(pairSplit[0], encoding), pairSplit.length > 1 ? HtmlUtil.urlDecode(pairSplit[1], encoding) : "");
            }
        }
        return params;
    }

    @Deprecated
    public static Map<String, String> extractParams(URL link) {
        return QueryStringUtil.toMap(link.getQuery());
    }

    @Deprecated
    public static String toString(Map<String, String> params) {
        return QueryStringUtil.toString(params, GeneralUtil.getDefaultCharset());
    }

    public static String toString(Map<String, String> params, Charset encoding) {
        StringBuilder sb = new StringBuilder();
        params.forEach((key, value) -> {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(HtmlUtil.urlEncode(key, encoding)).append('=').append(value != null ? HtmlUtil.urlEncode(value, encoding) : "");
        });
        return sb.toString();
    }
}

