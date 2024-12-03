/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.links;

import org.apache.commons.lang.StringUtils;

public class GenericLinkParser {
    private String originalLinkText;
    private String linkBody;
    private String notLinkBody;
    private String linkTitle;
    private String spaceKey;
    private String destinationTitle = "";
    private String anchor;
    private String shortcutName;
    private String shortcutValue;
    private String attachmentName;
    private long contentId;

    public GenericLinkParser(String linkText) {
        this.originalLinkText = linkText;
        if (linkText.indexOf("&#039;") != -1) {
            linkText = linkText.replaceAll("&#039;", "'");
        }
        StringBuffer buf = new StringBuffer(linkText);
        this.linkBody = this.extractLinkBody(buf);
        this.linkTitle = this.trimIfPossible(this.divideAfter(buf, '|'));
        this.notLinkBody = buf.toString().trim();
    }

    public void parseAsContentLink() {
        if (!this.notLinkBody.startsWith("~")) {
            StringBuffer shortcutBuf = new StringBuffer(this.notLinkBody);
            this.shortcutName = this.trimIfPossible(this.divideAfterLast(shortcutBuf, '@'));
            if (StringUtils.isNotBlank((String)this.shortcutName)) {
                this.shortcutValue = shortcutBuf.toString();
            }
        }
        StringBuffer buf = new StringBuffer(this.notLinkBody);
        if (StringUtils.isBlank((String)this.shortcutName)) {
            this.spaceKey = this.trimIfPossible(GenericLinkParser.divideOn(buf, ':'));
            if (buf.indexOf("$") == 0) {
                buf.deleteCharAt(0);
                this.contentId = this.extractNumber(buf);
                if (this.contentId == 0L) {
                    return;
                }
            }
            this.attachmentName = this.trimIfPossible(this.divideAfter(buf, '^'));
            this.anchor = this.trimIfPossible(this.divideAfter(buf, '#'));
        }
        if (this.contentId == 0L) {
            this.destinationTitle = buf.toString().trim();
        }
    }

    private long extractNumber(StringBuffer buf) {
        int i;
        StringBuffer digits = new StringBuffer(10);
        for (i = 0; i < buf.length() && Character.isDigit(buf.charAt(i)); ++i) {
            digits.append(buf.charAt(i));
        }
        if (i > 0) {
            buf.delete(0, i);
        }
        try {
            return Long.parseLong(digits.toString());
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String trimIfPossible(String s) {
        if (s == null) {
            return null;
        }
        return s.trim();
    }

    public String getOriginalLinkText() {
        return this.originalLinkText;
    }

    public String getLinkBody() {
        return this.linkBody;
    }

    public String getNotLinkBody() {
        return this.notLinkBody;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getDestinationTitle() {
        return this.destinationTitle;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public String getShortcutName() {
        return this.shortcutName;
    }

    public String getShortcutValue() {
        return this.shortcutValue;
    }

    public String getLinkTitle() {
        return this.linkTitle;
    }

    public String getAttachmentName() {
        return this.attachmentName;
    }

    public long getContentId() {
        return this.contentId;
    }

    private String extractLinkBody(StringBuffer buffer) {
        if (buffer.indexOf("!") == -1 || buffer.indexOf("!") > buffer.indexOf("|") || buffer.indexOf("!") == buffer.lastIndexOf("!")) {
            return GenericLinkParser.divideOn(buffer, '|');
        }
        StringBuffer body = new StringBuffer();
        boolean inEscape = false;
        for (int i = 0; i < buffer.length(); ++i) {
            char c = buffer.charAt(i);
            if (c == '!') {
                boolean bl = inEscape = !inEscape;
            }
            if (c == '|' && !inEscape) {
                buffer.delete(0, i + 1);
                return body.toString();
            }
            body.append(c);
        }
        return null;
    }

    public static String divideOn(StringBuffer buffer, char divider) {
        if (buffer.length() == 0) {
            return null;
        }
        int i = buffer.indexOf(Character.toString(divider));
        if (i < 0) {
            return null;
        }
        if (i == 0) {
            buffer.deleteCharAt(0);
            return null;
        }
        String body = buffer.substring(0, i);
        buffer.delete(0, i + 1);
        return body;
    }

    private String divideAfter(StringBuffer buffer, char divider) {
        if (buffer.length() == 0) {
            return null;
        }
        return this.divideAfter(buffer, buffer.indexOf(Character.toString(divider)));
    }

    private String divideAfterLast(StringBuffer buffer, char divider) {
        if (buffer.length() == 0) {
            return null;
        }
        return this.divideAfter(buffer, buffer.lastIndexOf(Character.toString(divider)));
    }

    private String divideAfter(StringBuffer buffer, int index) {
        if (index < 0) {
            return null;
        }
        if (index == buffer.length() - 1) {
            buffer.deleteCharAt(buffer.length() - 1);
            return null;
        }
        String body = buffer.substring(index + 1);
        buffer.delete(index, buffer.length());
        return body;
    }
}

