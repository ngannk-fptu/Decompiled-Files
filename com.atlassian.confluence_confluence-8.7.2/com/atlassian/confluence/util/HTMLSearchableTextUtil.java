/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.cyberneko.html.parsers.SAXParser
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageEmbeddedImageUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Deque;
import org.apache.commons.lang3.StringUtils;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class HTMLSearchableTextUtil {
    public static String stripTags(String htmlSource) throws SAXException {
        return HTMLSearchableTextUtil.stripTags("", htmlSource, new String[0]);
    }

    public static String stripTags(String htmlSource, String[] elementsToIgnore) throws SAXException {
        return HTMLSearchableTextUtil.stripTags("", htmlSource, elementsToIgnore);
    }

    public static String stripTags(String pageTitle, String htmlSource) throws SAXException {
        return HTMLSearchableTextUtil.stripTags(pageTitle, htmlSource, new String[0]);
    }

    public static String stripTags(String pageTitle, String htmlSource, String[] elementsToIgnore) throws SAXException {
        if (htmlSource == null) {
            return null;
        }
        try {
            SAXParser parser = new SAXParser();
            TagStripperSAXHandler handler = new TagStripperSAXHandler(pageTitle, elementsToIgnore);
            parser.setContentHandler((ContentHandler)handler);
            InputSource inputSource = new InputSource(new StringReader(htmlSource));
            parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setFeature("http://cyberneko.org/html/features/scanner/normalize-attrs", true);
            parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
            parser.parse(inputSource);
            return handler.getTextContent();
        }
        catch (IOException e) {
            throw new SAXException("IOException while parsing the HTML source", e);
        }
    }

    private static final class TagStripperSAXHandler
    extends DefaultHandler {
        private static final String[] BLOCK_LEVEL_ELEMENTS = new String[]{"address", "blockquote", "button", "dd", "div", "dl", "dt", "fieldset", "form", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "li", "map", "noscript", "object", "ol", "p", "pre", "script", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "ul"};
        private static final String LINE_BREAK_ELEMENT = "br";
        private static final String A_HREF_ELEMENT = "a";
        private static final String HREF_ATTRIBUTE = "href";
        private static final String[] QNAMES_TO_IGNORE = new String[]{"ac:default-parameter", "ac:parameter", "ac:property"};
        private final StringBuilder textContent = new StringBuilder();
        private final Deque<String> qnameIgnoreStack = Lists.newLinkedList();
        private final String[] elementsToIgnore;
        private boolean inLink;
        private StringBuilder linkTextContent;
        private String resourceTitle;
        private String pageTitle;
        private String hrefLink;
        private boolean isImage = false;
        private boolean isHref = false;

        public TagStripperSAXHandler(String pageTitle, String[] elementsToIgnore) {
            this.pageTitle = pageTitle;
            this.elementsToIgnore = elementsToIgnore;
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            if (this.qnameIgnoreStack.isEmpty()) {
                this.textContent.append(chars, start, length);
                if (this.inLink) {
                    this.linkTextContent.append(chars, start, length);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!this.qnameIgnoreStack.isEmpty() && StringUtils.isNotBlank((CharSequence)qName) && this.qnameIgnoreStack.peek().equals(qName)) {
                this.qnameIgnoreStack.pop();
            } else if (this.inLink && this.isLinkElement(localName)) {
                if (StringUtils.isBlank((CharSequence)this.linkTextContent.toString()) && StringUtils.isNotBlank((CharSequence)this.resourceTitle)) {
                    this.textContent.append(this.resourceTitle);
                } else if (StringUtils.isBlank((CharSequence)this.linkTextContent.toString()) && StringUtils.isBlank((CharSequence)this.resourceTitle)) {
                    this.textContent.append(this.pageTitle);
                }
                this.inLink = false;
                this.resourceTitle = null;
            } else if (this.isImage && this.isImageElement(localName) && StringUtils.isNotBlank((CharSequence)this.resourceTitle)) {
                this.textContent.append(this.resourceTitle);
                this.isImage = false;
                this.resourceTitle = null;
            } else if (this.isHref && this.isHrefElement(localName) && StringUtils.isNotBlank((CharSequence)this.hrefLink)) {
                this.textContent.append(" " + this.hrefLink);
                this.isHref = false;
                this.hrefLink = null;
            } else {
                this.blockElementHandling(localName);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (StringUtils.isNotBlank((CharSequence)qName) && (Arrays.binarySearch(QNAMES_TO_IGNORE, qName) >= 0 || Arrays.binarySearch(this.elementsToIgnore, localName) >= 0)) {
                this.qnameIgnoreStack.push(qName);
            } else if (this.isResourceIdentifierInLink(qName) && StringUtils.isBlank((CharSequence)this.resourceTitle)) {
                this.processResourceIdentifier(qName, attributes);
            } else if (this.isLinkElement(localName)) {
                this.inLink = true;
                this.linkTextContent = new StringBuilder();
            } else if (localName.equals(LINE_BREAK_ELEMENT)) {
                this.appendNewLine();
            } else if (qName.equals(A_HREF_ELEMENT)) {
                this.isHref = true;
                String link = attributes.getValue(HREF_ATTRIBUTE);
                if (StringUtils.isNotBlank((CharSequence)link) && !link.startsWith("/")) {
                    this.hrefLink = link;
                }
            } else if (this.isImageElement(localName)) {
                this.isImage = true;
            } else {
                this.blockElementHandling(localName);
            }
        }

        private boolean isResourceIdentifierInLink(String qName) {
            return (this.inLink || this.isImage) && qName.startsWith("ri:");
        }

        private void processResourceIdentifier(String qName, Attributes attributes) {
            if (qName.contains("page")) {
                this.resourceTitle = attributes.getValue("ri:content-title");
            } else if (qName.contains("blog-post")) {
                this.resourceTitle = attributes.getValue("ri:content-title");
            } else if (qName.contains("attachment")) {
                this.resourceTitle = attributes.getValue("ri:filename");
            } else if (qName.contains("user")) {
                UserKey userKey;
                ConfluenceUser user;
                String userKeyAttributeName = "ri:userkey";
                String userKeyAttributeValue = attributes.getValue("ri:userkey");
                if (StringUtils.isNotBlank((CharSequence)userKeyAttributeValue) && (user = FindUserHelper.getUserByUserKey(userKey = new UserKey(userKeyAttributeValue))) != null) {
                    this.resourceTitle = user.getName();
                }
            } else if (qName.contains("space")) {
                this.resourceTitle = attributes.getValue("ri:space-key");
            } else if (qName.contains("shortcut")) {
                this.resourceTitle = attributes.getValue("ri:parameter") + "@" + attributes.getValue("ri:key");
            } else if (qName.contains("url")) {
                this.resourceTitle = attributes.getValue("ri:value");
            } else if (qName.contains("content-entity")) {
                this.resourceTitle = attributes.getValue("ri:content-id");
            }
        }

        private boolean isLinkElement(String elementName) {
            return StorageLinkConstants.LINK_ELEMENT.getLocalPart().equals(elementName);
        }

        private boolean isImageElement(String elementName) {
            return StorageEmbeddedImageUnmarshaller.IMAGE_ELEMENT.getLocalPart().equals(elementName);
        }

        private boolean isHrefElement(String elementName) {
            return A_HREF_ELEMENT.equals(elementName);
        }

        private void blockElementHandling(String elementName) {
            if (Arrays.binarySearch(BLOCK_LEVEL_ELEMENTS, elementName) >= 0) {
                this.appendNewLine();
            }
        }

        private void appendNewLine() {
            this.textContent.append('\n');
        }

        public String getTextContent() {
            return this.textContent.toString();
        }
    }
}

