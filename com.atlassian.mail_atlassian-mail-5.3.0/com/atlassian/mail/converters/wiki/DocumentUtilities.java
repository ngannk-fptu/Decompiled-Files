/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.jsoup.Jsoup
 *  org.jsoup.internal.StringUtil
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 */
package com.atlassian.mail.converters.wiki;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@ParametersAreNonnullByDefault
final class DocumentUtilities {
    private DocumentUtilities() {
    }

    @Nonnull
    static DocumentElement parseHtml(String html) {
        Document document = Jsoup.parse((String)html);
        document.outputSettings().prettyPrint(false);
        return new DocumentElement(document);
    }

    @Nonnull
    static BodyElement getBody(DocumentElement document) {
        Element body = document.getDocument().body();
        return new BodyElement(body);
    }

    @Nonnull
    static String removeTrailingWhitespace(StringBuilder sb) {
        StringBuilder whiteSpace = new StringBuilder();
        while (sb.length() > 0 && StringUtil.isWhitespace((int)sb.charAt(sb.length() - 1))) {
            whiteSpace.append(sb.charAt(sb.length() - 1));
            sb.deleteCharAt(sb.length() - 1);
        }
        return whiteSpace.reverse().toString();
    }

    static class BodyElement {
        private final Element body;

        private BodyElement(Element body) {
            this.body = (Element)Preconditions.checkNotNull((Object)body, (Object)"Body can not be null");
            Preconditions.checkArgument((boolean)body.tag().getName().equals("body"), (Object)"Element must be the body");
        }

        public Element getBody() {
            return this.body;
        }
    }

    static class DocumentElement {
        private final Document document;

        private DocumentElement(Document document) {
            this.document = (Document)Preconditions.checkNotNull((Object)document, (Object)"Document can not be null");
        }

        public Document getDocument() {
            return this.document;
        }
    }
}

