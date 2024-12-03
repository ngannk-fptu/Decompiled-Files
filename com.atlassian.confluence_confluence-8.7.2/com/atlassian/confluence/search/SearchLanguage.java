/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.plugins.index.api.LanguageDescriptor;
import java.util.Arrays;

public enum SearchLanguage implements LanguageDescriptor
{
    ARABIC("arabic"),
    BRAZILIAN("brazilian"),
    CHINESE("chinese"),
    CJK("CJK"),
    CZECH("czech"),
    CUSTOM_JAPANESE("custom-japanese"),
    DANISH("danish"),
    DUTCH("dutch"),
    ENGLISH("english"),
    FINNISH("finnish"),
    FRENCH("french"),
    GERMAN("german"),
    GREEK("greek"),
    HUNGARIAN("hungarian"),
    ITALIAN("italian"),
    NORWEGIAN("norwegian"),
    PERSIAN("persian"),
    POLISH("polish"),
    ROMANIAN("romanian"),
    RUSSIAN("russian"),
    SPANISH("spanish"),
    SWEDISH("swedish"),
    OTHER("other");

    public final String value;

    private SearchLanguage(String value) {
        this.value = value;
    }

    public static SearchLanguage fromString(String value) {
        return Arrays.stream(SearchLanguage.values()).filter(item -> item.value.equals(value)).findFirst().orElse(OTHER);
    }
}

