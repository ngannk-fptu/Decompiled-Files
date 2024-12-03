/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.radeox.util.Encoder
 */
package com.atlassian.renderer.util;

import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.opensymphony.util.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.radeox.util.Encoder;

public class UrlUtil {
    public static final List URL_PROTOCOLS = Collections.unmodifiableList(Arrays.asList("http://", "https://", "ftp://", "ftps://", "mailto:", "nntp://", "news://", "irc://", "file:"));
    protected static final int URL_SCHEMA_LENGTH = 8;

    public static String escapeSpecialCharacters(String url) {
        if (url == null) {
            return null;
        }
        return HtmlEscaper.escapeAmpersands(url, true);
    }

    public static boolean startsWithUrl(String str) {
        if (!TextUtils.stringSet((String)str)) {
            return false;
        }
        return UrlUtil.getUrlIndex(str) == 0;
    }

    public static boolean containsUrl(String str) {
        if (!TextUtils.stringSet((String)str)) {
            return false;
        }
        return UrlUtil.getUrlIndex(str) != -1;
    }

    public static int getUrlIndex(String str) {
        if (!TextUtils.stringSet((String)str)) {
            return -1;
        }
        String str_lower = str.toLowerCase();
        for (String protocol : URL_PROTOCOLS) {
            int index = str_lower.indexOf(protocol);
            if (index == -1 || index != 0 && Character.isLetterOrDigit(str_lower.charAt(index - 1))) continue;
            return index;
        }
        return -1;
    }

    public static String escapeUrlFirstCharacter(String linkBody) {
        int i = UrlUtil.getUrlIndex(linkBody);
        if (i == 0) {
            StringBuffer buf = new StringBuffer(linkBody);
            char c = buf.charAt(i);
            buf.deleteCharAt(i);
            buf.insert(i, Encoder.toEntity((int)c));
            linkBody = buf.toString();
        }
        return linkBody;
    }

    public static String correctBaseUrls(String html, String baseUrl) {
        if (html.length() < 10) {
            return html;
        }
        StringBuffer result = new StringBuffer(html.length());
        int idx = 0;
        while (true) {
            String matchText = "";
            int matchIdx = html.length() + 1;
            String[] linkText = UrlUtil.linksToFix();
            for (int i = 0; i < linkText.length; ++i) {
                int testIdx = html.indexOf(linkText[i], idx);
                if (testIdx < 0 || testIdx >= matchIdx) continue;
                matchText = linkText[i];
                matchIdx = testIdx;
            }
            if (matchIdx > html.length()) break;
            result.append(html.substring(idx, matchIdx += matchText.length()));
            String linkStart = html.substring(matchIdx, Math.min(matchIdx + 8, html.length()));
            if (UrlUtil.isLocalUrl(linkStart)) {
                if (linkStart.startsWith("/")) {
                    result.append(UrlUtil.getServerUrl(baseUrl));
                } else {
                    result.append(UrlUtil.getUrlPath(baseUrl)).append("/");
                }
            }
            idx = matchIdx;
        }
        result.append(html.substring(idx));
        return result.toString();
    }

    private static boolean isLocalUrl(String url) {
        String[] validProtocols = new String[]{"http://", "https://", "mailto:", "ftp://"};
        for (int i = 0; i < validProtocols.length; ++i) {
            String validProtocol = validProtocols[i];
            if (!url.startsWith(validProtocol)) continue;
            return false;
        }
        return true;
    }

    private static String getUrlPath(String baseUrl) {
        int lastSlash;
        String result = baseUrl;
        if (result.indexOf(63) > 0) {
            result = result.substring(0, result.indexOf(63));
        }
        if ((lastSlash = result.lastIndexOf(47)) >= 8) {
            result = result.substring(0, lastSlash);
        }
        return result;
    }

    private static String getServerUrl(String baseUrl) {
        String result = baseUrl;
        int firstSlash = result.indexOf(47, 8);
        if (firstSlash >= 0) {
            result = result.substring(0, firstSlash);
        }
        return result;
    }

    private static String[] linksToFix() {
        return new String[]{" href=\"", " href='", " src=\"", " src='"};
    }

    public static String buildNewRelativeUrl(HttpServletRequest request, String name, String value) {
        StringBuffer url = new StringBuffer(request.getContextPath());
        url.append(request.getServletPath());
        if (request.getPathInfo() != null) {
            url.append(request.getPathInfo());
        }
        url.append("?");
        Map params = request.getParameterMap();
        boolean paramAppended = false;
        Iterator iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            String paramName = (String)iterator.next();
            if (name.equals(paramName)) {
                UrlUtil.appendParam(url, name, value);
                paramAppended = true;
            } else {
                UrlUtil.appendParam(url, paramName, ((String[])params.get(paramName))[0]);
            }
            if (!iterator.hasNext()) continue;
            url.append('&');
        }
        if (!paramAppended) {
            url.append("&");
            UrlUtil.appendParam(url, name, value);
        }
        return url.toString();
    }

    public static Map getQueryParameters(String url) {
        String query;
        HashMap<String, String> parameters = new HashMap<String, String>();
        if (url.indexOf("?") != -1 && (query = url.substring(url.indexOf("?") + 1)) != null) {
            String[] queryList = query.split("&");
            for (int i = 0; i < queryList.length; ++i) {
                String queryParameter = queryList[i];
                String[] queryParameters = queryParameter.split("=");
                if (queryParameters != null && queryParameters.length == 2) {
                    parameters.put(queryParameters[0], queryParameters[1]);
                    continue;
                }
                parameters.put(queryParameter, null);
            }
        }
        return parameters;
    }

    private static void appendParam(StringBuffer url, String paramName, String value) {
        url.append(UrlUtil.urlEncode(paramName)).append("=").append(UrlUtil.urlEncode(value));
    }

    private static String urlEncode(String url) {
        if (url == null) {
            return null;
        }
        try {
            return URLEncoder.encode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return url;
        }
    }
}

