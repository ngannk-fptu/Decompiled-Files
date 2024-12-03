/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.google.common.base.Function
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.CompleteURLEncoder;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.google.common.base.Function;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HtmlUtil {
    private static final Logger log = LoggerFactory.getLogger(HtmlUtil.class);
    private static final Pattern URL_ENCODED_STRING_PATTERN = Pattern.compile("%[a-fA-F0-9]{2}");
    private static final String[] URL_ENCODING_EXCEPTIONS_FIND = new String[]{"%40", "%7E"};
    private static final String[] URL_ENCODING_EXCEPTIONS_REPLACE = new String[]{"@", "~"};
    public static final HtmlUtil INSTANCE = new HtmlUtil();
    @Deprecated(forRemoval=true)
    public static final Function<String, String> HTML_ENCODE_FUNCTION = HtmlUtil::htmlEncode;

    @HtmlSafe
    public static String htmlEncode(Object object) {
        if (object instanceof Number) {
            return object.toString();
        }
        if (object instanceof String) {
            return HtmlUtil.htmlEncode((String)object);
        }
        return "";
    }

    @HtmlSafe
    public static String htmlEncode(String text) {
        return PlainTextToHtmlConverter.encodeHtmlEntities(text);
    }

    @HtmlSafe
    public static String htmlEncodeAndReplaceSpaces(String text) {
        return PlainTextToHtmlConverter.matchAndReplaceSpaces(PlainTextToHtmlConverter.encodeHtmlEntities(text));
    }

    @Deprecated
    public static String completeUrlEncode(String url) {
        return HtmlUtil.completeUrlEncode(url, GeneralUtil.getDefaultCharset());
    }

    public static String completeUrlEncode(String url, Charset encoding) {
        if (url == null) {
            return null;
        }
        try {
            return CompleteURLEncoder.encode(url, encoding);
        }
        catch (MalformedURLException e) {
            log.error("Error while trying to encode URL {}", (Object)url, (Object)e);
            return url;
        }
    }

    @Deprecated
    public static String urlEncode(String text) {
        if (text == null) {
            return null;
        }
        try {
            return HtmlUtil.urlEncode(text, GeneralUtil.getDefaultCharset());
        }
        catch (RuntimeException e) {
            log.error("Error while trying to encode string {}", (Object)text, (Object)e);
            return text;
        }
    }

    @Deprecated
    public static String urlEncode(String text, String encoding) {
        try {
            return HtmlUtil.urlEncode(text, Charset.forName(encoding));
        }
        catch (RuntimeException ex) {
            log.error("Error while trying to encode string {} with encoding {}", new Object[]{text, encoding, ex});
            return text;
        }
    }

    public static String urlEncode(String text, Charset encoding) {
        String s = URLEncoder.encode(text, encoding);
        return StringUtils.replaceEach((String)s, (String[])URL_ENCODING_EXCEPTIONS_FIND, (String[])URL_ENCODING_EXCEPTIONS_REPLACE);
    }

    @Deprecated
    public static String urlDecode(String url) {
        return HtmlUtil.urlDecode(url, GeneralUtil.getDefaultCharset());
    }

    @Deprecated
    public static String urlDecode(String url, String encoding) {
        return HtmlUtil.urlDecode(url, Charset.forName(encoding));
    }

    public static String urlDecode(String url, Charset encoding) {
        if (url == null) {
            return null;
        }
        try {
            return URLDecoder.decode(url, encoding);
        }
        catch (IllegalArgumentException e) {
            log.warn("Error while trying to decode URL {} with encoding {}", (Object)url, (Object)encoding);
            log.debug("Stack trace", (Throwable)e);
            return url;
        }
    }

    public static String resoluteUrlDecode(String encodedStr, Charset charset) {
        Objects.requireNonNull(encodedStr, "String");
        Objects.requireNonNull(charset, "Charset");
        return new String(HtmlUtil.resoluteUrlDecode(encodedStr.getBytes(StandardCharsets.UTF_8)), charset);
    }

    private static byte[] resoluteUrlDecode(byte[] bytes) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; ++i) {
            int initI = i;
            byte b = bytes[i];
            if (b == 43) {
                buffer.write(32);
                continue;
            }
            if (b == 37) {
                try {
                    int u = Character.digit((char)bytes[++i], 16);
                    int l = Character.digit((char)bytes[++i], 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException();
                    }
                    buffer.write((char)((u << 4) + l));
                }
                catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    buffer.write(b);
                    i = initI;
                }
                continue;
            }
            buffer.write(b);
        }
        return buffer.toByteArray();
    }

    public static String loopedUrlDecode(String str, Charset charset) {
        String prev;
        do {
            prev = str;
            try {
                str = URLDecoder.decode(prev, charset);
            }
            catch (IllegalArgumentException e) {
                str = HtmlUtil.resoluteUrlDecode(prev, charset);
            }
        } while (!str.equals(prev));
        return str;
    }

    public static String loopedUrlDecode(String str) {
        return HtmlUtil.loopedUrlDecode(str, GeneralUtil.getDefaultCharset());
    }

    public static boolean shouldUrlDecode(String text) {
        return text != null && (URL_ENCODED_STRING_PATTERN.matcher(text).find() || text.contains("+"));
    }

    @Deprecated
    public static String reencodeURL(String originalUrl) {
        return HtmlUtil.reencodeURL(originalUrl, GeneralUtil.getDefaultCharset());
    }

    public static String reencodeURL(String originalUrl, Charset encoding) {
        String url;
        if (originalUrl == null) {
            return null;
        }
        StringBuilder fragment = new StringBuilder();
        int fragmentOffset = originalUrl.indexOf(35);
        if (fragmentOffset == -1) {
            url = originalUrl;
        } else {
            fragment.append(originalUrl.substring(fragmentOffset));
            url = originalUrl.substring(0, fragmentOffset);
        }
        int queryOffset = url.indexOf(63);
        if (queryOffset == -1) {
            return url;
        }
        String mainUrl = url.substring(0, queryOffset);
        String query = url.substring(queryOffset + 1);
        Scanner scanner = new Scanner(query).useDelimiter("&");
        try {
            StringBuilder reencodedQuery = new StringBuilder();
            while (scanner.hasNext()) {
                String nameValuePair = scanner.next();
                int equalsOffset = nameValuePair.indexOf(61);
                if (equalsOffset != -1) {
                    String name = nameValuePair.substring(0, equalsOffset);
                    String value = nameValuePair.substring(equalsOffset + 1);
                    reencodedQuery.append(HtmlUtil.urlEncode(HtmlUtil.urlDecode(name, encoding), encoding)).append('=').append(HtmlUtil.urlEncode(HtmlUtil.urlDecode(value, encoding), encoding));
                } else {
                    reencodedQuery.append(HtmlUtil.urlEncode(HtmlUtil.urlDecode(nameValuePair, encoding), encoding));
                }
                if (!scanner.hasNext()) continue;
                reencodedQuery.append('&');
            }
            String string = mainUrl + "?" + reencodedQuery + fragment;
            if (scanner != null) {
                scanner.close();
            }
            return string;
        }
        catch (Throwable throwable) {
            try {
                if (scanner != null) {
                    try {
                        scanner.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (RuntimeException e) {
                log.error("Error while trying to reencode URL {}", (Object)originalUrl, (Object)e);
                return originalUrl;
            }
        }
    }
}

