/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompleteURLEncoder {
    private static final Logger log = LoggerFactory.getLogger(CompleteURLEncoder.class);
    public static final String FILE_PROTOCOL = "file";

    @Deprecated
    public static String encode(String urlString, String enc) throws MalformedURLException, UnsupportedEncodingException {
        return CompleteURLEncoder.encode(urlString, Charset.forName(enc));
    }

    public static String encode(String urlString, Charset enc) throws MalformedURLException {
        log.debug(urlString);
        URL url = new URL(urlString);
        if (FILE_PROTOCOL.equals(url.getProtocol())) {
            return CompleteURLEncoder.fileEncode(url);
        }
        return CompleteURLEncoder.httpEncode(url, enc);
    }

    private static String fileEncode(URL url) throws MalformedURLException {
        try {
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), null, null);
            URL encodedUrl = uri.toURL();
            if (!StringUtils.isNotEmpty((CharSequence)url.getHost())) {
                return "file://" + encodedUrl.getPath();
            }
            return encodedUrl.toString();
        }
        catch (URISyntaxException e) {
            log.error("Error while encoding file url: " + url, (Throwable)e);
            return url.toString();
        }
    }

    private static String httpEncode(URL url, Charset enc) throws MalformedURLException {
        try {
            boolean hasQueryString;
            String query = url.getQuery();
            StringBuffer stringBuffer = null;
            if (StringUtils.isNotEmpty((CharSequence)query)) {
                stringBuffer = new StringBuffer();
                StringTokenizer st = new StringTokenizer(query, "&");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    String[] tokens = token.split("=");
                    if (tokens != null && tokens.length == 2) {
                        stringBuffer.append(URLEncoder.encode(tokens[0], enc)).append("=").append(URLEncoder.encode(tokens[1], enc));
                    } else {
                        stringBuffer.append(URLEncoder.encode(token, enc));
                    }
                    if (!st.hasMoreTokens()) continue;
                    stringBuffer.append("&");
                }
            }
            String urlString = url.toString();
            int fragmentIndex = urlString.indexOf("#");
            boolean bl = hasQueryString = stringBuffer != null;
            if (fragmentIndex != -1) {
                if (!hasQueryString) {
                    stringBuffer = new StringBuffer();
                }
                String fragment = urlString.substring(fragmentIndex + 1, urlString.length());
                stringBuffer.append("#");
                stringBuffer.append(fragment);
            }
            String queryParam = null;
            if (hasQueryString) {
                queryParam = "";
            }
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), queryParam, null);
            URL encodedUrl = uri.toURL();
            String returnUrl = encodedUrl.toString();
            StringBuilder total = new StringBuilder(returnUrl);
            if (stringBuffer != null) {
                total.append(stringBuffer);
            }
            log.debug("encoded http url: " + returnUrl);
            return total.toString();
        }
        catch (URISyntaxException e) {
            log.error("Error while encoding http url: " + url, (Throwable)e);
            return url.toString();
        }
    }
}

