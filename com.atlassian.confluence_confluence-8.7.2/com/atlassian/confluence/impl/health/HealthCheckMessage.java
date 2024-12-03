/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.confluence.impl.health;

import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import org.apache.commons.lang3.StringEscapeUtils;

public class HealthCheckMessage {
    private final String headline;
    private final String html;
    private final String text;

    private HealthCheckMessage(String html, String text, String headline) {
        this.html = html;
        this.text = text;
        this.headline = headline;
    }

    public String asHtml() {
        return this.html;
    }

    public String asText() {
        return this.text;
    }

    public String getHeadline() {
        return this.headline;
    }

    public static class Builder {
        private static final String LINK_ELEMENT_FORMAT = "<a href=\"%s\"%s>%s</a>";
        private final StringBuilder html = new StringBuilder();
        private final StringBuilder text = new StringBuilder();
        private String headline;

        private static String openingTag(String tag) {
            return "<" + tag + ">";
        }

        private static String closingTag(String tag) {
            return "</" + tag + ">";
        }

        public HealthCheckMessage build() {
            return new HealthCheckMessage(this.html.toString(), this.text.toString(), this.headline);
        }

        public Builder withHeading(String headline) {
            this.headline = headline;
            return this;
        }

        public Builder append(String s) {
            this.html.append(StringEscapeUtils.escapeHtml4((String)s));
            this.text.append(s);
            return this;
        }

        public Builder lineBreak() {
            this.html.append("<br/>");
            this.text.append(System.lineSeparator());
            return this;
        }

        public Builder tag(String tag, String tagContents) {
            this.html.append(Builder.openingTag(tag)).append(tagContents).append(Builder.closingTag(tag));
            this.text.append(tagContents);
            return this;
        }

        public Builder appendLink(String url, String linkText, boolean newTab) {
            this.html.append(Builder.getLinkAsHtml(url, newTab, linkText));
            String logMessage = linkText + " (" + url + ")";
            this.text.append(logMessage);
            return this;
        }

        @VisibleForTesting
        static String getLinkAsHtml(String url, boolean newTab, String linkText) {
            String targetAttribute = newTab ? " target=\"_blank\"" : "";
            return String.format(LINK_ELEMENT_FORMAT, url, targetAttribute, linkText);
        }

        public Builder appendList(String ... items) {
            return this.appendList(Arrays.asList(items));
        }

        public Builder appendList(Iterable<String> items) {
            this.html.append(Builder.openingTag("ul"));
            for (String item : items) {
                this.html.append(Builder.openingTag("li")).append(item).append(Builder.closingTag("li"));
                this.text.append("\t- ").append(item).append(System.lineSeparator());
            }
            this.html.append(Builder.closingTag("ul"));
            return this;
        }
    }
}

