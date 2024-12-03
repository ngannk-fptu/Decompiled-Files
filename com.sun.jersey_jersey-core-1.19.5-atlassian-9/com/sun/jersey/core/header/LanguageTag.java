/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Locale;

public class LanguageTag {
    public static final int MAX_SUBTAG_LENGTH = 8;
    protected String tag;
    protected String primaryTag;
    protected String subTags;

    protected LanguageTag() {
    }

    public static LanguageTag valueOf(String s) throws IllegalArgumentException {
        LanguageTag lt = new LanguageTag();
        try {
            lt.parse(s);
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
        return lt;
    }

    public LanguageTag(String primaryTag, String subTags) {
        this.tag = subTags != null && subTags.length() > 0 ? primaryTag + "-" + subTags : primaryTag;
        this.primaryTag = primaryTag;
        this.subTags = subTags;
    }

    public LanguageTag(String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }

    public LanguageTag(HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.tag = reader.nextToken();
        if (reader.hasNext()) {
            throw new ParseException("Invalid Language tag", reader.getIndex());
        }
        this.parse(this.tag);
    }

    public final boolean isCompatible(Locale tag) {
        if (this.tag.equals("*")) {
            return true;
        }
        if (this.subTags == null) {
            return this.primaryTag.equalsIgnoreCase(tag.getLanguage());
        }
        return this.primaryTag.equalsIgnoreCase(tag.getLanguage()) && this.subTags.equalsIgnoreCase(tag.getCountry());
    }

    public final Locale getAsLocale() {
        return this.subTags == null ? new Locale(this.primaryTag) : new Locale(this.primaryTag, this.subTags);
    }

    protected final void parse(String languageTag) throws ParseException {
        if (!this.isValid(languageTag)) {
            throw new ParseException("String, " + languageTag + ", is not a valid language tag", 0);
        }
        int index = languageTag.indexOf(45);
        if (index == -1) {
            this.primaryTag = languageTag;
            this.subTags = null;
        } else {
            this.primaryTag = languageTag.substring(0, index);
            this.subTags = languageTag.substring(index + 1, languageTag.length());
        }
    }

    private boolean isValid(String tag) {
        int alphanumCount = 0;
        int dash = 0;
        for (int i = 0; i < tag.length(); ++i) {
            char c = tag.charAt(i);
            if (c == '-') {
                if (alphanumCount == 0) {
                    return false;
                }
                alphanumCount = 0;
                ++dash;
                continue;
            }
            if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || dash > 0 && '0' <= c && c <= '9') {
                if (++alphanumCount <= 8) continue;
                return false;
            }
            return false;
        }
        return alphanumCount != 0;
    }

    public final String getTag() {
        return this.tag;
    }

    public final String getPrimaryTag() {
        return this.primaryTag;
    }

    public final String getSubTags() {
        return this.subTags;
    }

    public boolean equals(Object object) {
        if (object instanceof LanguageTag) {
            LanguageTag lt = (LanguageTag)object;
            if (this.tag != null) {
                if (!this.tag.equals(lt.getTag())) {
                    return false;
                }
                if (lt.getTag() != null) {
                    return false;
                }
            }
            if (this.primaryTag != null) {
                if (!this.primaryTag.equals(lt.getPrimaryTag())) {
                    return false;
                }
                if (lt.getPrimaryTag() != null) {
                    return false;
                }
            }
            if (this.subTags != null) {
                if (!this.subTags.equals(lt.getSubTags())) {
                    return false;
                }
                if (lt.getSubTags() != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.tag == null ? 0 : this.tag.hashCode()) + (this.primaryTag == null ? 0 : this.primaryTag.hashCode()) + (this.subTags == null ? 0 : this.primaryTag.hashCode());
    }

    public String toString() {
        return this.primaryTag + (this.subTags == null ? "" : this.subTags);
    }
}

