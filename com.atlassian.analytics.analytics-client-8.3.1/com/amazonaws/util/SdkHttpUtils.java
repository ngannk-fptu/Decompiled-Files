/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.SignableRequest;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.util.BasicNameValuePair;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.URLEncodedUtils;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SdkHttpUtils {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Pattern ENCODED_CHARACTERS_PATTERN;

    public static String urlEncode(String value, boolean path) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, DEFAULT_ENCODING);
            Matcher matcher = ENCODED_CHARACTERS_PATTERN.matcher(encoded);
            StringBuffer buffer = new StringBuffer(encoded.length());
            while (matcher.find()) {
                String replacement = matcher.group(0);
                if ("+".equals(replacement)) {
                    replacement = "%20";
                } else if ("*".equals(replacement)) {
                    replacement = "%2A";
                } else if ("%7E".equals(replacement)) {
                    replacement = "~";
                } else if (path && "%2F".equals(replacement)) {
                    replacement = "/";
                }
                matcher.appendReplacement(buffer, replacement);
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String urlDecode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, DEFAULT_ENCODING);
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean isUsingNonDefaultPort(URI uri) {
        String scheme = StringUtils.lowerCase(uri.getScheme());
        int port = uri.getPort();
        if (port <= 0) {
            return false;
        }
        if (scheme.equals("http") && port == 80) {
            return false;
        }
        return !scheme.equals("https") || port != 443;
    }

    public static boolean usePayloadForQueryParameters(SignableRequest<?> request) {
        boolean requestIsPOST = HttpMethodName.POST.equals((Object)request.getHttpMethod());
        boolean requestHasNoPayload = request.getContent() == null;
        return requestIsPOST && requestHasNoPayload;
    }

    public static String encodeParameters(SignableRequest<?> request) {
        Map<String, List<String>> requestParams = request.getParameters();
        if (requestParams.isEmpty()) {
            return null;
        }
        ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        for (Map.Entry<String, List<String>> entry : requestParams.entrySet()) {
            String parameterName = entry.getKey();
            for (String value : entry.getValue()) {
                nameValuePairs.add(new BasicNameValuePair(parameterName, value));
            }
        }
        return URLEncodedUtils.format(nameValuePairs, DEFAULT_ENCODING);
    }

    public static String appendUri(String baseUri, String path) {
        return SdkHttpUtils.appendUri(baseUri, path, false);
    }

    public static String appendUri(String baseUri, String path, boolean escapeDoubleSlash) {
        String resultUri = baseUri;
        if (path != null && path.length() > 0) {
            if (path.startsWith("/")) {
                if (resultUri.endsWith("/")) {
                    resultUri = resultUri.substring(0, resultUri.length() - 1);
                }
            } else if (!resultUri.endsWith("/")) {
                resultUri = resultUri + "/";
            }
            resultUri = escapeDoubleSlash ? resultUri + path.replace("//", "/%2F") : resultUri + path;
        } else if (!resultUri.endsWith("/")) {
            resultUri = resultUri + "/";
        }
        return resultUri;
    }

    static {
        StringBuilder pattern = new StringBuilder();
        pattern.append(Pattern.quote("+")).append("|").append(Pattern.quote("*")).append("|").append(Pattern.quote("%7E")).append("|").append(Pattern.quote("%2F"));
        ENCODED_CHARACTERS_PATTERN = Pattern.compile(pattern.toString());
    }
}

