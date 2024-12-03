/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.model;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import org.apache.commons.text.StringEscapeUtils;

public class HtmlString {
    private final String value;

    private HtmlString(String value) {
        this.value = (String)Preconditions.checkNotNull((Object)value);
    }

    public static HtmlString html(String value) {
        return new HtmlString(value);
    }

    public String getHtml() {
        return this.value;
    }

    public String toString() {
        return "Html(" + StringEscapeUtils.escapeHtml4(this.value) + ")";
    }

    public boolean equals(Object other) {
        return other instanceof HtmlString && ((HtmlString)other).value.equals(this.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public static Function<HtmlString, String> htmlToString() {
        return HtmlString::getHtml;
    }

    public static Function<String, HtmlString> stringToHtml() {
        return HtmlString::html;
    }
}

