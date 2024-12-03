/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.internal.StringUtil
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 *  org.jsoup.nodes.TextNode
 *  org.jsoup.select.NodeTraversor
 *  org.jsoup.select.NodeVisitor
 */
package com.atlassian.mail.converters.wiki;

import com.atlassian.mail.MailUtils;
import com.atlassian.mail.converters.HtmlConverter;
import com.atlassian.mail.converters.wiki.BlockStyleHandler;
import com.atlassian.mail.converters.wiki.ColorHandler;
import com.atlassian.mail.converters.wiki.DocumentUtilities;
import com.atlassian.mail.converters.wiki.FontStyleHandler;
import com.atlassian.mail.converters.wiki.LinkAndImageHandler;
import com.atlassian.mail.converters.wiki.ListHandler;
import com.atlassian.mail.converters.wiki.TableHandler;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class HtmlToWikiTextConverter
implements HtmlConverter {
    private static final char WIKI_LINK_START_CHAR = '[';
    private static final char WIKI_TABLE_CHAR = '|';
    private static final char NEWLINE_CHAR = '\n';
    private static final String NEWLINE = Character.toString('\n');
    private static final String WIKI_TABLE = Character.toString('|');
    private static final String NON_WIKI_TEXT_REPLACE = ":";
    private static final String WIKI_LINK_START = Character.toString('[');
    private static final String WIKI_LINK_END = "]";
    private final List<MailUtils.Attachment> attachments;
    private final boolean thumbnailsAllowed;

    public HtmlToWikiTextConverter(@Nonnull List<MailUtils.Attachment> attachments) {
        this(attachments, true);
    }

    public HtmlToWikiTextConverter(@Nonnull List<MailUtils.Attachment> attachments, boolean thumbnailsAllowed) {
        this.attachments = ImmutableList.copyOf(attachments);
        this.thumbnailsAllowed = thumbnailsAllowed;
    }

    @Override
    public String convert(@Nonnull String html) throws IOException {
        DocumentUtilities.DocumentElement document = DocumentUtilities.parseHtml(html);
        DocumentUtilities.BodyElement body = DocumentUtilities.getBody(document);
        FormattingVisitor formatter = new FormattingVisitor();
        NodeTraversor traversor = new NodeTraversor();
        NodeTraversor.traverse((NodeVisitor)formatter, (Node)body.getBody());
        return formatter.toString();
    }

    static /* synthetic */ List access$100(HtmlToWikiTextConverter x0) {
        return x0.attachments;
    }

    static /* synthetic */ boolean access$200(HtmlToWikiTextConverter x0) {
        return x0.thumbnailsAllowed;
    }

    private class FormattingVisitor
    implements NodeVisitor {
        private final StringBuilder accum = new StringBuilder();
        private final StringBuilder colorAccum = new StringBuilder();
        private final BlockStyleHandler blockStyleHandler = new BlockStyleHandler();
        private final FontStyleHandler fontStyleHandler = new FontStyleHandler(this.blockStyleHandler);
        private final ListHandler listHandler = new ListHandler(this.blockStyleHandler);
        private final TableHandler tableHandler = new TableHandler(this.blockStyleHandler);
        private final ColorHandler colorHandler = new ColorHandler(this.fontStyleHandler, this.blockStyleHandler);
        private final LinkAndImageHandler linkAndImageHandler = new LinkAndImageHandler(this.blockStyleHandler, this.colorHandler, HtmlToWikiTextConverter.access$100(HtmlToWikiTextConverter.this), HtmlToWikiTextConverter.access$200(HtmlToWikiTextConverter.this));

        private FormattingVisitor() {
        }

        public void head(Node node, int depth) {
            String name = node.nodeName();
            if (node instanceof TextNode) {
                String text = ((TextNode)node).text();
                if (this.fontStyleHandler.isAnyAtStart(depth)) {
                    text = StringUtils.stripStart((String)text, null);
                }
                if (this.tableHandler.isInTable()) {
                    text = StringUtils.replace((String)text, (String)WIKI_TABLE, (String)HtmlToWikiTextConverter.NON_WIKI_TEXT_REPLACE);
                }
                if (this.linkAndImageHandler.isInsideLinkWithText()) {
                    text = StringUtils.replace((String)text, (String)WIKI_LINK_START, (String)HtmlToWikiTextConverter.NON_WIKI_TEXT_REPLACE);
                    text = StringUtils.replace((String)text, (String)HtmlToWikiTextConverter.WIKI_LINK_END, (String)HtmlToWikiTextConverter.NON_WIKI_TEXT_REPLACE);
                }
                if (this.tableHandler.isInTable()) {
                    this.append(node, text, WhitespaceNewlineHandling.ONE);
                } else if (this.linkAndImageHandler.isInsideLinkWithText()) {
                    this.append(node, text, WhitespaceNewlineHandling.NONE);
                } else {
                    this.append(node, text);
                }
            } else if (StringUtil.in((String)name, (String[])new String[]{"b", "strong", "i", "em", "u", "ins", "strike", "del", "s", "q", "cite"})) {
                Element element;
                String enterText = this.fontStyleHandler.enter(node, name, depth);
                if (StringUtils.isNotEmpty((CharSequence)enterText) && node instanceof Element && (element = (Element)node).hasText()) {
                    String removedWhitespace = "";
                    Node firstChildNode = element.childNodeSize() > 0 ? element.childNode(0) : null;
                    String text = firstChildNode instanceof TextNode ? ((TextNode)firstChildNode).getWholeText() : element.text();
                    while (text.length() > 0 && StringUtil.isWhitespace((int)text.charAt(0))) {
                        removedWhitespace = removedWhitespace + text.charAt(0);
                        text = StringUtils.substring((String)text, (int)1);
                    }
                    if (!this.fontStyleHandler.isPrecededByStyle(this.accum.toString())) {
                        this.append(node, (String)StringUtils.defaultIfEmpty((CharSequence)removedWhitespace, (CharSequence)" "));
                    }
                }
                this.append(node, enterText);
            } else if (StringUtil.in((String)name, (String[])new String[]{"blockquote", "pre", "code"})) {
                this.appendAroundColor(node, this.blockStyleHandler.enter(node, name, depth));
            } else if (StringUtil.in((String)name, (String[])new String[]{"h1", "h2", "h3", "h4", "h5", "h6"})) {
                this.appendAroundColor(node, NEWLINE + name + ". ");
            } else if (StringUtil.in((String)name, (String[])new String[]{"ol", "ul", "li", "dl", "dd", "dt"})) {
                String enter = this.listHandler.enter(name);
                if (StringUtil.in((String)name, (String[])new String[]{"ol", "ul", "dl"})) {
                    this.append(node, enter, WhitespaceNewlineHandling.ONE);
                } else {
                    String prefix = "";
                    if (!this.tableHandler.isInTable()) {
                        prefix = NEWLINE;
                    }
                    this.appendAroundColor(node, prefix + enter, WhitespaceNewlineHandling.ONE);
                }
            } else if (StringUtil.in((String)name, (String[])new String[]{"table", "tr", "th", "td"})) {
                Object whitespaceNewlineHandling = WhitespaceNewlineHandling.NONE;
                if (StringUtil.in((String)name, (String[])new String[]{"table", "tr"}) || this.tableHandler.isFirstTableRowData()) {
                    whitespaceNewlineHandling = WhitespaceNewlineHandling.ONE;
                }
                this.appendAroundColor(node, this.tableHandler.enter(name), (WhitespaceNewlineHandling)((Object)whitespaceNewlineHandling));
            } else if (StringUtil.in((String)name, (String[])new String[]{"a", "img"})) {
                this.appendAroundColor(node, this.linkAndImageHandler.enter(node, name));
            } else if (StringUtil.in((String)name, (String[])new String[]{"p", "div"})) {
                if (name.equals("div")) {
                    for (Node child : node.childNodes()) {
                        boolean treatNewline;
                        if (child instanceof TextNode) {
                            String text = ((TextNode)child).text();
                            if (StringUtils.isBlank((CharSequence)text)) continue;
                            treatNewline = true;
                        } else {
                            treatNewline = child instanceof Element ? !StringUtil.in((String)((Element)child).tagName(), (String[])new String[]{"br", "p"}) : true;
                        }
                        if (treatNewline) {
                            this.append(node, NEWLINE);
                        }
                        break;
                    }
                } else if (name.equals("p")) {
                    this.append(node, NEWLINE + NEWLINE);
                }
            }
            String color = this.colorHandler.enter(this.accum, node, name, depth, this.linkAndImageHandler.isInsideAnyLink(), this.linkAndImageHandler.isInsideLinkWithText(), this.linkAndImageHandler.isUrlInLinkText(), this.tableHandler.isEndOfRow());
            if (StringUtils.isNotBlank((CharSequence)color)) {
                this.colorAccum.append(color);
            }
        }

        public void tail(Node node, int depth) {
            String name = node.nodeName();
            if (name.equals("br")) {
                this.append(node, NEWLINE);
            } else if (name.equals("hr")) {
                this.appendAroundColor(node, NEWLINE + "----" + NEWLINE, WhitespaceNewlineHandling.ONE);
            } else if (StringUtil.in((String)name, (String[])new String[]{"b", "strong", "i", "em", "u", "ins", "strike", "del", "s", "q", "cite"})) {
                String text;
                Element element;
                Node sibling;
                String fontExit = this.fontStyleHandler.exit(name, depth);
                String removedWhitespace = "";
                if (StringUtils.isNotBlank((CharSequence)fontExit)) {
                    removedWhitespace = DocumentUtilities.removeTrailingWhitespace(this.accum);
                    while (this.accum.length() > 0 && fontExit.length() > 0 && this.accum.charAt(this.accum.length() - 1) == fontExit.charAt(0)) {
                        this.accum.deleteCharAt(this.accum.length() - 1);
                        fontExit = StringUtils.substring((String)fontExit, (int)1);
                        removedWhitespace = DocumentUtilities.removeTrailingWhitespace(this.accum) + removedWhitespace;
                    }
                }
                this.append(node, fontExit);
                if (node instanceof Element && (sibling = (element = (Element)node).nextSibling()) != null && sibling instanceof TextNode && StringUtils.isNotEmpty((CharSequence)(text = ((TextNode)sibling).text())) && !StringUtil.isWhitespace((int)text.charAt(0)) && !Pattern.matches("\\p{Punct}", StringUtils.substring((String)text, (int)0, (int)1))) {
                    this.append(node, " ");
                }
                this.append(node, removedWhitespace);
            } else if (StringUtil.in((String)name, (String[])new String[]{"blockquote", "pre", "code"})) {
                this.append(node, this.blockStyleHandler.exit(name, depth));
            } else if (StringUtil.in((String)name, (String[])new String[]{"h1", "h2", "h3", "h4", "h5", "h6"})) {
                this.appendAroundColor(node, NEWLINE);
            } else if (StringUtil.in((String)name, (String[])new String[]{"ol", "ul", "li", "dl", "dd", "dt"})) {
                this.append(node, this.listHandler.exit(name), WhitespaceNewlineHandling.ONE);
            } else if (StringUtil.in((String)name, (String[])new String[]{"table", "tr", "th", "td"})) {
                String exit = this.tableHandler.exit(name);
                if (StringUtil.in((String)name, (String[])new String[]{"table", "tr"})) {
                    this.appendAroundColor(node, exit, WhitespaceNewlineHandling.ONE);
                } else {
                    this.appendAroundColor(node, exit, WhitespaceNewlineHandling.NONE);
                }
            } else if ("a".equals(name)) {
                this.append(node, this.linkAndImageHandler.exit(this.accum, node, name));
            }
            String precedingWhitespace = DocumentUtilities.removeTrailingWhitespace(this.accum);
            String color = this.colorHandler.exit(this.accum, name, depth, precedingWhitespace, this.linkAndImageHandler.isInsideAnyLink(), this.linkAndImageHandler.isUrlInLinkText());
            if (StringUtils.isNotBlank((CharSequence)color)) {
                this.append(node, color);
            } else {
                this.accum.append(precedingWhitespace);
            }
        }

        private void appendAroundColor(Node node, String text) {
            this.appendAroundColor(node, text, WhitespaceNewlineHandling.UNCHANGED);
        }

        private void appendAroundColor(Node node, String text, WhitespaceNewlineHandling whitespaceNewlineHandling) {
            String precedingWhitespace = DocumentUtilities.removeTrailingWhitespace(this.accum);
            boolean tableRow = "tr".equals(node.nodeName());
            String s = this.colorHandler.handleAroundNonSupportedFormatting(this.accum, text, precedingWhitespace, this.linkAndImageHandler.isInsideAnyLink(), this.linkAndImageHandler.isInsideLinkWithText(), this.linkAndImageHandler.isUrlInLinkText(), tableRow);
            if (StringUtils.isNotEmpty((CharSequence)s)) {
                this.append(node, s, whitespaceNewlineHandling);
            } else {
                this.accum.append(precedingWhitespace);
            }
        }

        private void append(Node node, String text) {
            this.append(node, text, WhitespaceNewlineHandling.UNCHANGED);
        }

        private void append(Node node, String text, WhitespaceNewlineHandling whitespaceNewlineHandling) {
            char last;
            text = StringUtils.replace((String)text, (String)("\r" + NEWLINE), (String)NEWLINE);
            text = StringUtils.replace((String)text, (String)"\f", (String)NEWLINE);
            text = StringUtils.replace((String)text, (String)"\r", (String)NEWLINE);
            text = StringUtils.replace((String)text, (String)"\t", (String)" ");
            if (this.accum.length() == 0) {
                text = StringUtils.stripStart((String)text, null);
            } else if (whitespaceNewlineHandling == WhitespaceNewlineHandling.NONE || whitespaceNewlineHandling == WhitespaceNewlineHandling.ONE) {
                String removed = DocumentUtilities.removeTrailingWhitespace(this.accum);
                ColorHandler.StripResult removedFormatting = this.colorHandler.removeFormatting(text);
                String result = StringUtils.defaultString((String)removedFormatting.getResult(), (String)"");
                while (StringUtils.isNotEmpty((CharSequence)result) && StringUtil.isWhitespace((int)result.charAt(0))) {
                    removed = removed + result.charAt(0);
                    result = StringUtils.substring((String)result, (int)1);
                }
                text = StringUtils.containsAny((CharSequence)removed, (CharSequence)NEWLINE) && StringUtils.isNotEmpty((CharSequence)removedFormatting.getRemoved()) ? removedFormatting.getRemoved() + NEWLINE + result : removedFormatting.getRemoved() + result;
                if (StringUtils.containsAny((CharSequence)removed, (CharSequence)" ")) {
                    if (this.linkAndImageHandler.isInsideLinkWithText()) {
                        if (this.accum.charAt(this.accum.length() - 1) != '[') {
                            this.doAppending(' ');
                        }
                    } else {
                        this.doAppending(' ');
                    }
                }
                if (whitespaceNewlineHandling == WhitespaceNewlineHandling.ONE && StringUtils.contains((CharSequence)removed, (int)10) && !this.tableHandler.isStartOfTableData(node) && (!this.tableHandler.isInTable() || this.tableHandler.isEndOfRow() || this.tableHandler.isFirstTableRowData())) {
                    this.doAppending(NEWLINE);
                }
            }
            if (StringUtils.startsWith((CharSequence)text, (CharSequence)NEWLINE)) {
                char last2 = this.accum.charAt(this.accum.length() - 1);
                if (this.tableHandler.isStartOfTableData(node) && (StringUtil.isWhitespace((int)last2) || last2 == '|')) {
                    text = StringUtils.strip((String)text, (String)NEWLINE);
                }
            }
            if (this.accum.length() > 0 && text.length() > 0 && this.tableHandler.isFirstTableRowData() && !this.linkAndImageHandler.isInsideLinkWithText() && StringUtils.contains((CharSequence)StringUtils.trimToEmpty((String)text), (int)124) && (last = this.accum.charAt(this.accum.length() - 1)) != '\n') {
                this.doAppending(NEWLINE);
            }
            this.doAppending(text);
            this.linkAndImageHandler.reset();
        }

        private void doAppending(char character) {
            this.doAppending(String.valueOf(character));
        }

        private void doAppending(String string) {
            if (this.colorAccum.length() > 0) {
                this.accum.append((CharSequence)this.colorAccum);
                this.colorAccum.delete(0, this.colorAccum.length());
            }
            this.fontStyleHandler.wrapStylesAround(this.accum, string);
        }

        public String toString() {
            return this.accum.toString();
        }
    }

    private static enum WhitespaceNewlineHandling {
        NONE,
        ONE,
        UNCHANGED;

    }
}

