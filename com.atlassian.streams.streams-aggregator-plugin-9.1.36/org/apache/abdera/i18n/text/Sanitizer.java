/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Filter;
import org.apache.abdera.i18n.text.Normalizer;
import org.apache.abdera.i18n.text.UrlEncoding;

public class Sanitizer {
    public static final String SANITIZE_PATTERN = "[^A-Za-z0-9\\%!$&\\\\'()*+,;=_]+";
    private static final Filter PathNoDelimFilter = new Filter(){

        public boolean accept(int c) {
            return !CharUtils.isAlphaDigit(c) && c != 45 && c != 46 && c != 95 && c != 126 && c != 38 && c != 61 && c != 43 && c != 36 && c != 44 && c != 59 && c != 37;
        }
    };

    public static String sanitize(String slug) {
        return Sanitizer.sanitize(slug, null, false, null, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler) {
        return Sanitizer.sanitize(slug, filler, false, null, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler, boolean lower) {
        return Sanitizer.sanitize(slug, filler, lower, null, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler, String pattern) {
        return Sanitizer.sanitize(slug, filler, false, null, pattern);
    }

    public static String sanitize(String slug, String filler, boolean lower, String pattern) {
        return Sanitizer.sanitize(slug, filler, lower, null, pattern);
    }

    public static String sanitize(String slug, String filler, boolean lower, Normalizer.Form form) {
        return Sanitizer.sanitize(slug, filler, lower, form, SANITIZE_PATTERN);
    }

    public static String sanitize(String slug, String filler, boolean lower, Normalizer.Form form, String pattern) {
        if (slug == null) {
            return null;
        }
        if (lower) {
            slug = slug.toLowerCase();
        }
        if (form != null) {
            try {
                slug = Normalizer.normalize(slug, form);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        slug = slug.replaceAll("\\s+", "_");
        slug = filler != null ? slug.replaceAll(pattern, filler) : UrlEncoding.encode((CharSequence)slug, PathNoDelimFilter);
        return slug;
    }
}

