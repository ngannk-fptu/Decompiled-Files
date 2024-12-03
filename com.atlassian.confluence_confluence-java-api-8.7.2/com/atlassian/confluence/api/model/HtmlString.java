/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public class HtmlString {
    private String html;

    @JsonCreator
    public HtmlString(String html) {
        this.html = html;
    }

    @JsonValue
    public String getHtml() {
        return this.html;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HtmlString that = (HtmlString)o;
        return Objects.equals(this.html, that.html);
    }

    public int hashCode() {
        return this.html != null ? this.html.hashCode() : 0;
    }

    public String toString() {
        return "HtmlString{html='" + this.html + '\'' + '}';
    }
}

