/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.functions;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionContext;
import org.tuckey.web.filters.urlrewrite.substitution.SubstitutionFilterChain;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.URLDecoder;
import org.tuckey.web.filters.urlrewrite.utils.URLEncoder;

public class StringFunctions {
    private static Log log = Log.getLog(StringFunctions.class);
    private static final Pattern FIND_COLON_PATTERN = Pattern.compile("(?<!\\\\):");
    private static final Pattern FIND_ENCODING_PATTERN = Pattern.compile("^[0-9a-zA-Z-]+:");

    public static String toLower(String s, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        return s == null ? null : nextFilter.substitute(s, ctx).toLowerCase();
    }

    public static String toUpper(String s, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        return s == null ? null : nextFilter.substitute(s, ctx).toUpperCase();
    }

    public static String trim(String str, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        if (str == null) {
            return null;
        }
        return nextFilter.substitute(str, ctx).trim();
    }

    public static String length(String str, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        if (str == null) {
            return "0";
        }
        return String.valueOf(nextFilter.substitute(str, ctx).length());
    }

    public static String escape(String subject, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        String encoding = "UTF-8";
        if (FIND_ENCODING_PATTERN.matcher(subject).find()) {
            encoding = subject.substring(0, subject.indexOf(58));
            subject = subject.substring(subject.indexOf(58) + 1);
            if (!Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }
        }
        subject = nextFilter.substitute(subject, ctx);
        try {
            return java.net.URLEncoder.encode(subject, encoding);
        }
        catch (UnsupportedEncodingException e) {
            log.error(e, e);
            return "";
        }
    }

    public static String escapePath(String subject, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        String encoding = "UTF-8";
        if (FIND_ENCODING_PATTERN.matcher(subject).find()) {
            encoding = subject.substring(0, subject.indexOf(58));
            subject = subject.substring(subject.indexOf(58) + 1);
            if (!Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }
        }
        subject = nextFilter.substitute(subject, ctx);
        try {
            return URLEncoder.encodePathSegment(subject, encoding);
        }
        catch (UnsupportedEncodingException e) {
            log.error(e, e);
            return "";
        }
    }

    public static String unescape(String subject, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        String encoding = "UTF-8";
        if (FIND_ENCODING_PATTERN.matcher(subject).find()) {
            encoding = subject.substring(0, subject.indexOf(58));
            subject = subject.substring(subject.indexOf(58) + 1);
            if (!Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }
        }
        subject = nextFilter.substitute(subject, ctx);
        try {
            return java.net.URLDecoder.decode(subject, encoding);
        }
        catch (UnsupportedEncodingException e) {
            log.error(e, e);
            return "";
        }
    }

    public static String unescapePath(String subject, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        String encoding = "UTF-8";
        if (FIND_ENCODING_PATTERN.matcher(subject).find()) {
            encoding = subject.substring(0, subject.indexOf(58));
            subject = subject.substring(subject.indexOf(58) + 1);
            if (!Charset.isSupported(encoding)) {
                encoding = "UTF-8";
            }
        }
        subject = nextFilter.substitute(subject, ctx);
        try {
            return URLDecoder.decodePath(subject, encoding);
        }
        catch (URISyntaxException e) {
            log.error(e, e);
            return "";
        }
    }

    public static String replaceAll(String subject, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        String replace = "";
        String with = "";
        if (FIND_COLON_PATTERN.matcher(subject).find()) {
            replace = subject.substring(subject.indexOf(58) + 1);
            subject = subject.substring(0, subject.indexOf(58));
            if (FIND_COLON_PATTERN.matcher(replace).find()) {
                with = replace.substring(replace.indexOf(58) + 1);
                replace = replace.substring(0, replace.indexOf(58));
            }
        }
        subject = nextFilter.substitute(subject, ctx);
        return subject.replaceAll(replace, with);
    }

    public static String replaceFirst(String subject, SubstitutionFilterChain nextFilter, SubstitutionContext ctx) {
        String replace = "";
        String with = "";
        if (FIND_COLON_PATTERN.matcher(subject).find()) {
            replace = subject.substring(subject.indexOf(58) + 1);
            subject = subject.substring(0, subject.indexOf(58));
            if (FIND_COLON_PATTERN.matcher(replace).find()) {
                with = replace.substring(replace.indexOf(58) + 1);
                replace = replace.substring(0, replace.indexOf(58));
            }
        }
        subject = nextFilter.substitute(subject, ctx);
        return subject.replaceFirst(replace, with);
    }
}

