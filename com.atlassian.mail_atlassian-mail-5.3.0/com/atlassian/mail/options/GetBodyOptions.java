/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.mail.options;

import com.atlassian.mail.converters.HtmlConverter;
import com.atlassian.mail.converters.basic.HtmlToTextConverter;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetBodyOptions {
    private static final HtmlConverter HTML_TO_TEXT_CONVERTER = new HtmlToTextConverter();
    public static final GetBodyOptions PREFER_TEXT_BODY_NO_STRIP_WHITESPACE = new GetBodyOptions(false, false, HTML_TO_TEXT_CONVERTER);
    public static final GetBodyOptions PREFER_TEXT_BODY_STRIP_WHITESPACE = new GetBodyOptions(true, false, HTML_TO_TEXT_CONVERTER);
    private final boolean stripWhitespace;
    private final boolean preferHtmlPart;
    private final HtmlConverter htmlConverter;

    private GetBodyOptions(boolean stripWhitespace, boolean preferHtmlPart, HtmlConverter htmlConverter) {
        this.stripWhitespace = stripWhitespace;
        this.preferHtmlPart = preferHtmlPart;
        this.htmlConverter = htmlConverter;
    }

    public boolean isStripWhitespace() {
        return this.stripWhitespace;
    }

    public boolean isPreferHtmlPart() {
        return this.preferHtmlPart;
    }

    public HtmlConverter getHtmlConverter() {
        return this.htmlConverter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(GetBodyOptions template) {
        return new Builder(template);
    }

    public static class Builder {
        private boolean stripWhitespace;
        private boolean preferHtmlPart;
        private HtmlConverter htmlConverter;

        private Builder() {
            this.htmlConverter = HTML_TO_TEXT_CONVERTER;
        }

        private Builder(GetBodyOptions template) {
            this.stripWhitespace = template.stripWhitespace;
            this.preferHtmlPart = template.preferHtmlPart;
            this.htmlConverter = template.htmlConverter;
        }

        public Builder stripWhitespace() {
            return this.setStripWhitespace(true);
        }

        public Builder setStripWhitespace(boolean stripWhitespace) {
            this.stripWhitespace = stripWhitespace;
            return this;
        }

        public Builder preferHtmlPart() {
            return this.setPreferHtmlPart(true);
        }

        public Builder setPreferHtmlPart(boolean preferHtmlPart) {
            this.preferHtmlPart = preferHtmlPart;
            return this;
        }

        public Builder setHtmlConverter(HtmlConverter htmlConverter) {
            this.htmlConverter = htmlConverter;
            return this;
        }

        public GetBodyOptions build() {
            return new GetBodyOptions(this.stripWhitespace, this.preferHtmlPart, this.htmlConverter);
        }
    }
}

