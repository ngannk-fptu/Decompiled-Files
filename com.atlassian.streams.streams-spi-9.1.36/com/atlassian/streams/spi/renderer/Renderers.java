/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.Html
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.spi.renderer;

import com.atlassian.streams.api.Html;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;

public final class Renderers {
    private static final int EXCERPT_LIMIT = 250;
    private static final HtmlToken EndOfInput = new HtmlToken(){};

    public static String render(TemplateRenderer renderer, String template, Map<String, ?> context) {
        StringWriter writer = new StringWriter();
        try {
            Map<String, ?> ctx = context;
            renderer.render(template, ctx, (Writer)writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error rendering " + template + " template", e);
        }
        return Renderers.unescapeLineBreaks(writer.toString());
    }

    public static String getExcerpt(String strippedContent) {
        return Renderers.getExcerptUsingLimit(strippedContent, 250);
    }

    public static String getExcerptUsingLimit(String strippedContent, int limit) {
        if (strippedContent.length() > limit) {
            int index = strippedContent.lastIndexOf(" ", limit);
            return strippedContent.substring(0, index > 0 ? index : limit) + "...";
        }
        return strippedContent;
    }

    @Deprecated
    public static Function<String, String> stripBasicMarkup() {
        return StripBasicMarkup.INSTANCE;
    }

    public static String stripBasicMarkup(String content) {
        if (StringUtils.isBlank((CharSequence)content)) {
            return content;
        }
        content = content.replaceAll("!([^ \\t\\r\\n\\f\\\\!]+)!", " ");
        content = content.replaceAll("(?<!\\\\)\\{gadget[\\S]*\\{gadget}", "");
        content = content.replaceAll("(?<!\\{)\\{cs:[^\\}]+\\}:?", "");
        content = content.replace("&nbsp;", " ");
        content = content.replaceAll("h[0-9]\\.", " ");
        content = content.replaceAll("\\[.*///.*\\]", "");
        content = content.replaceAll("(^|\\W)(?<!\\\\)[\\[\\]\\*_\\^\\-\\~\\+]+(\\w)", "$1$2");
        content = content.replaceAll("(\\w)(?<!\\\\)[\\[\\]\\*_\\^\\-\\~\\+]+(\\W|$)", "$1$2");
        content = content.replaceAll("\\|", " ");
        content = content.replaceAll("(?<!\\\\)\\{([^:\\}\\{]+)(?::([^\\}\\{]*))?\\}(?!\\})", " ");
        content = content.replaceAll("\\n", "<br>");
        content = content.replaceAll("\\r", "<br>");
        content = content.replaceAll("bq\\.", " ");
        content = content.replaceAll("  ", " ");
        content = content.replace("\\", "");
        content = content.replace("#", "");
        content = content.replace("{{", "");
        content = content.replace("}}", "");
        content = content.replace("\u00a0", " ");
        content = content.replaceAll("[ \\t\\f]+", " ");
        content = content.replaceAll("<br>( *(<br>))+", "<br><br>");
        return content;
    }

    public static String replaceNbsp(String before) {
        return before.replaceAll("&nbsp;", " ").replaceAll("&amp;nbsp;", " ");
    }

    @Deprecated
    public static Function<String, String> unescapeLineBreaks() {
        return UnescapeLineBreaks.INSTANCE;
    }

    public static String unescapeLineBreaks(String s) {
        return s.replaceAll("&lt;br&gt;", "<br>");
    }

    public static Html truncate(int len, Html html) {
        Stack<HtmlOpenTag> stack = new Stack<HtmlOpenTag>();
        StringBuilder sb = new StringBuilder();
        int currentLength = 0;
        HtmlTokenizer tokens = new HtmlTokenizer(html.toString());
        HtmlToken token = tokens.next();
        while (token != EndOfInput && currentLength < len) {
            if (token instanceof SelfClosingTag) {
                sb.append(token);
            } else if (token instanceof HtmlOpenTag) {
                stack.push((HtmlOpenTag)token);
                sb.append(token);
            } else if (token instanceof HtmlCloseTag) {
                HtmlCloseTag closeTag = (HtmlCloseTag)token;
                if (stack.isEmpty()) {
                    sb.append(token);
                } else {
                    while (!((HtmlOpenTag)stack.peek()).getName().equalsIgnoreCase(closeTag.getName()) && ((HtmlOpenTag)stack.peek()).missingCloseTagAllowed()) {
                        stack.pop();
                    }
                    if (((HtmlOpenTag)stack.peek()).getName().equalsIgnoreCase(closeTag.getName())) {
                        stack.pop();
                        sb.append(token);
                    } else {
                        boolean found = false;
                        for (HtmlOpenTag openTag : stack) {
                            if (!openTag.getName().equalsIgnoreCase(closeTag.getName())) continue;
                            found = true;
                            break;
                        }
                        if (found) {
                            while (!((HtmlOpenTag)stack.peek()).getName().equalsIgnoreCase(closeTag.getName())) {
                                sb.append(new HtmlCloseTag(((HtmlOpenTag)stack.pop()).getName()));
                            }
                            sb.append(token);
                        }
                    }
                }
            } else if (token instanceof HtmlChars) {
                String s = token.toString();
                if (s.length() + currentLength > len) {
                    int spaceIndex = s.indexOf(32);
                    if (currentLength == 0 && (spaceIndex == -1 || spaceIndex > len)) {
                        sb.append(s.substring(0, len));
                    } else {
                        int lastSpaceIndex = s.lastIndexOf(" ", len - currentLength);
                        sb.append(s.substring(0, lastSpaceIndex > 0 ? lastSpaceIndex : s.length()));
                    }
                    currentLength = len;
                } else {
                    currentLength += token.toString().length();
                    sb.append(s);
                }
            } else {
                sb.append(token);
                ++currentLength;
            }
            token = tokens.next();
        }
        if (currentLength < len) {
            return html;
        }
        while (!stack.isEmpty()) {
            if (((HtmlOpenTag)stack.peek()).missingCloseTagAllowed()) {
                stack.pop();
                continue;
            }
            if (((HtmlOpenTag)stack.peek()).getName().equalsIgnoreCase("script")) {
                int i = sb.toString().toLowerCase().lastIndexOf("<script");
                sb.delete(i, sb.length());
                stack.pop();
                continue;
            }
            sb.append(new HtmlCloseTag(((HtmlOpenTag)stack.pop()).getName()));
        }
        return new Html(sb.toString());
    }

    @Deprecated
    public static Function<Html, Html> truncate(int len) {
        return new Truncate(len);
    }

    public static Html replaceText(String searchFor, String replaceWith, Html html) {
        if (!html.toString().contains(searchFor)) {
            return html;
        }
        StringBuilder sb = new StringBuilder();
        HtmlTokenizer tokens = new HtmlTokenizer(html.toString(), false);
        HtmlToken token = tokens.next();
        while (token != EndOfInput) {
            if (token instanceof HtmlChars) {
                sb.append(token.toString().replace(searchFor, replaceWith));
            } else {
                sb.append(token);
            }
            token = tokens.next();
        }
        return Html.html((String)sb.toString());
    }

    @Deprecated
    public static Function<Html, Html> replaceText(String searchFor, String replaceWith) {
        return new ReplaceText(searchFor, replaceWith);
    }

    public static java.util.function.Function<Html, Html> replaceTextFunc(String searchFor, String replaceWith) {
        return new ReplaceText(searchFor, replaceWith);
    }

    @Deprecated
    public static Function<Html, Html> replaceTextWithHyperlink(String searchFor, URI linkUri) {
        return new ReplaceText(searchFor, "<a href=\"" + linkUri + "\">" + searchFor + "</a>");
    }

    public static java.util.function.Function<Html, Html> replaceTextWithHyperlinkFunc(String searchFor, URI linkUri) {
        return new ReplaceText(searchFor, "<a href=\"" + linkUri + "\">" + searchFor + "</a>");
    }

    private static final class HtmlTokenizer {
        private final String html;
        private final boolean tokenizeEntity;
        private int index = 0;

        public HtmlTokenizer(String html) {
            this(html, true);
        }

        public HtmlTokenizer(String html, boolean tokenizeEntity) {
            this.html = html;
            this.tokenizeEntity = tokenizeEntity;
        }

        public HtmlToken next() {
            if (this.index >= this.html.length()) {
                return EndOfInput;
            }
            char c = this.html.charAt(this.index);
            ++this.index;
            if (this.tokenizeEntity && c == '&') {
                return this.newEntity();
            }
            if (c != '<') {
                return this.newHtmlChars(c);
            }
            if (this.html.charAt(this.index) == '/') {
                ++this.index;
                return this.newCloseTag();
            }
            return this.newOpenTag();
        }

        private HtmlToken newOpenTag() {
            char c = this.html.charAt(this.index);
            StringBuilder tagName = new StringBuilder();
            while (c != '>' && c != '/' && !Character.isWhitespace(c) && c != '\u0000') {
                tagName.append(c);
                c = this.nextChar();
            }
            StringBuilder attributes = new StringBuilder();
            while (c != '>' && c != '\u0000') {
                attributes.append(c);
                c = this.nextChar();
            }
            ++this.index;
            if (attributes.length() > 0 && attributes.charAt(attributes.length() - 1) == '/') {
                return new SelfClosingTag(tagName.toString(), attributes.substring(0, attributes.length() - 1));
            }
            return new HtmlOpenTag(tagName.toString(), attributes.toString());
        }

        private HtmlToken newCloseTag() {
            char c = this.html.charAt(this.index);
            StringBuilder tag = new StringBuilder();
            while (c != '>' && c != '\u0000') {
                tag.append(c);
                c = this.nextChar();
            }
            ++this.index;
            return new HtmlCloseTag(tag.toString());
        }

        private HtmlToken newHtmlChars(char c) {
            StringBuilder sb = new StringBuilder().append(c);
            while (!(this.index >= this.html.length() || this.html.charAt(this.index) == '&' && this.tokenizeEntity || this.html.charAt(this.index) == '<')) {
                sb.append(this.html.charAt(this.index));
                ++this.index;
            }
            return new HtmlChars(sb.toString());
        }

        private HtmlToken newEntity() {
            char c = this.html.charAt(this.index);
            StringBuilder entity = new StringBuilder();
            while (c != ';' && c != '\u0000') {
                entity.append(c);
                c = this.nextChar();
            }
            ++this.index;
            return new HtmlEntity(entity.toString());
        }

        private char nextChar() {
            if (this.index < this.html.length() - 1) {
                ++this.index;
                return this.html.charAt(this.index);
            }
            return '\u0000';
        }
    }

    private static final class HtmlCloseTag
    implements HtmlToken {
        private final String name;

        HtmlCloseTag(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        public String toString() {
            return String.format("</%s>", this.name);
        }
    }

    private static final class SelfClosingTag
    extends HtmlOpenTag {
        SelfClosingTag(String name, String attributes) {
            super(name, attributes);
        }

        @Override
        public String toString() {
            return String.format("<%s%s/>", this.name, this.attributes);
        }
    }

    private static class HtmlOpenTag
    implements HtmlToken {
        private static final Set<String> allowsMissingClosedTag = ImmutableSet.of((Object)"br", (Object)"img", (Object)"input", (Object)"tr", (Object)"td", (Object)"th", (Object[])new String[]{"colgroup", "col"});
        protected final String name;
        protected final String attributes;

        HtmlOpenTag(String name, String attributes) {
            this.name = name;
            this.attributes = attributes;
        }

        String getName() {
            return this.name;
        }

        public boolean missingCloseTagAllowed() {
            return allowsMissingClosedTag.contains(this.name.toLowerCase());
        }

        public String toString() {
            return String.format("<%s%s>", this.name, this.attributes);
        }
    }

    private static final class HtmlEntity
    implements HtmlToken {
        private final String entity;

        HtmlEntity(String entity) {
            this.entity = entity;
        }

        public String toString() {
            return String.format("&%s;", this.entity);
        }
    }

    private static final class HtmlChars
    implements HtmlToken {
        private final String str;

        HtmlChars(String str) {
            this.str = str;
        }

        public String toString() {
            return this.str;
        }
    }

    private static interface HtmlToken {
    }

    public static class ReplaceText
    implements Function<Html, Html> {
        private final String searchFor;
        private final String replaceWith;

        public ReplaceText(String searchFor, String replaceWith) {
            this.searchFor = searchFor;
            this.replaceWith = replaceWith;
        }

        public Html apply(Html from) {
            return Renderers.replaceText(this.searchFor, this.replaceWith, from);
        }
    }

    @Deprecated
    private static class Truncate
    implements Function<Html, Html> {
        private final int len;

        public Truncate(int len) {
            this.len = len;
        }

        public Html apply(Html h) {
            return Renderers.truncate(this.len, h);
        }
    }

    @Deprecated
    private static enum UnescapeLineBreaks implements Function<String, String>
    {
        INSTANCE;


        public String apply(String s) {
            return s.replaceAll("&lt;br&gt;", "<br>");
        }
    }

    @Deprecated
    private static enum StripBasicMarkup implements Function<String, String>
    {
        INSTANCE;


        public String apply(String content) {
            return Renderers.stripBasicMarkup(content);
        }
    }
}

