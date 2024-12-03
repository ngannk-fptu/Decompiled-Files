/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mail.converters.wiki;

import com.atlassian.mail.MailUtils;
import com.atlassian.mail.converters.wiki.BlockStyleHandler;
import com.atlassian.mail.converters.wiki.ColorHandler;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
final class LinkAndImageHandler {
    private static final Logger log = LoggerFactory.getLogger(LinkAndImageHandler.class);
    public static final String HTML_LINK = "a";
    public static final String HTML_IMG = "img";
    public static final String HTML_HREF = "href";
    private static final String CONTENT_ID_PREFIX = "cid:";
    private static final String WIKI_LINK_OPEN_BRACKET = "[";
    private static final String WIKI_LINK_CLOSE_BRACKET = "]";
    private static final String WIKI_LINK_SEPARATOR = "|";
    private static final String WIKI_MACRO_ATTACHMENT_FORMAT = "!%s!";
    private static final String WIKI_MACRO_THUMBNAIL_FORMAT = "!%s|thumbnail!";
    private static final Pattern THUMBNAIL_PATTERN = Pattern.compile("(?i)^image/(jpeg|jpg|png|pjpeg)");
    private static final String NON_WIKI_TEXT_FORMAT = " <%s>";
    static final List<String> NON_WIKI_LINK_URIS = ImmutableList.of((Object)"tel:", (Object)"sms:", (Object)"callto:", (Object)"fax:", (Object)"modem:", (Object)"wtai:", (Object)"rhomailto:");
    private final BlockStyleHandler blockStyleHandler;
    private final ColorHandler colorHandler;
    private final List<MailUtils.Attachment> attachments;
    private final boolean thumbnailsAllowed;
    private boolean linkNonWikiHref;
    private boolean inLink;
    private boolean linkHasText;
    private boolean imageInsideLink;
    private boolean urlInLinkText;
    private boolean toReset;

    private static String WIKI_MACRO_ATTACHMENT(String value) {
        return String.format(WIKI_MACRO_ATTACHMENT_FORMAT, value);
    }

    private static String WIKI_IMAGE_MACRO(String value, @Nullable String contentType, boolean thumbnailsAllowed) {
        if (thumbnailsAllowed && THUMBNAIL_PATTERN.matcher(StringUtils.trimToEmpty((String)contentType)).find()) {
            return String.format(WIKI_MACRO_THUMBNAIL_FORMAT, value);
        }
        return LinkAndImageHandler.WIKI_MACRO_ATTACHMENT(value);
    }

    private static String NON_WIKI_TEXT(String value) {
        return String.format(NON_WIKI_TEXT_FORMAT, value);
    }

    public LinkAndImageHandler(BlockStyleHandler blockStyleHandler, ColorHandler colorHandler, List<MailUtils.Attachment> attachments, boolean thumbnailsAllowed) {
        this.blockStyleHandler = blockStyleHandler;
        this.colorHandler = colorHandler;
        this.attachments = ImmutableList.copyOf(attachments);
        this.thumbnailsAllowed = thumbnailsAllowed;
    }

    public String enter(Node node, String name) {
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        if (HTML_LINK.equals(name)) {
            this.inLink = false;
            this.linkHasText = false;
            this.imageInsideLink = false;
            this.urlInLinkText = false;
            this.toReset = false;
            final String href = node.attr(HTML_HREF);
            this.linkNonWikiHref = Iterables.any(NON_WIKI_LINK_URIS, (Predicate)new Predicate<String>(){

                public boolean apply(@Nullable String input) {
                    return StringUtils.startsWithIgnoreCase((CharSequence)href, (CharSequence)input);
                }
            });
            if (!this.linkNonWikiHref && node instanceof Element) {
                Element element = (Element)node;
                if (element.select(HTML_IMG).isEmpty()) {
                    this.inLink = true;
                    this.linkHasText = element.hasText();
                    if (this.linkHasText) {
                        this.urlInLinkText = LinkAndImageHandler.containsUrlInLinkText(element);
                    }
                    return WIKI_LINK_OPEN_BRACKET;
                }
                this.imageInsideLink = true;
            }
        } else if (HTML_IMG.equals(name)) {
            return this.replaceWikiMacroForImage(node);
        }
        return "";
    }

    public String exit(StringBuilder accum, Node node, String name) {
        if (!this.blockStyleHandler.isFormattingPossible()) {
            return "";
        }
        String val = "";
        if (HTML_LINK.equals(name)) {
            String href = node.attr(HTML_HREF);
            if (this.linkNonWikiHref) {
                val = val + LinkAndImageHandler.NON_WIKI_TEXT(href);
            } else if (this.imageInsideLink) {
                val = LinkAndImageHandler.NON_WIKI_TEXT(WIKI_LINK_OPEN_BRACKET + href + WIKI_LINK_CLOSE_BRACKET);
            } else {
                if (this.linkHasText) {
                    val = val + WIKI_LINK_SEPARATOR;
                }
                val = val + href;
                val = val + WIKI_LINK_CLOSE_BRACKET;
                val = this.colorHandler.handleAroundNonSupportedFormatting(accum, val, "", false, false, false, false);
            }
            this.toReset = true;
        }
        return val;
    }

    public boolean isInsideLinkWithText() {
        return this.isInsideAnyLink() && this.linkHasText;
    }

    public boolean isInsideAnyLink() {
        return this.inLink;
    }

    public boolean isUrlInLinkText() {
        return this.isInsideLinkWithText() && this.urlInLinkText;
    }

    public void reset() {
        if (this.toReset) {
            this.inLink = false;
            this.linkHasText = false;
            this.imageInsideLink = false;
            this.linkNonWikiHref = false;
            this.urlInLinkText = false;
        }
        this.toReset = false;
    }

    private String replaceWikiMacroForImage(Node node) {
        String imageSource = node.attr("src");
        if (StringUtils.startsWithIgnoreCase((CharSequence)imageSource, (CharSequence)CONTENT_ID_PREFIX)) {
            String contentId = StringUtils.removeStart((String)imageSource, (String)CONTENT_ID_PREFIX);
            MailUtils.Attachment image = this.getAttachmentById(contentId);
            if (image != null) {
                return LinkAndImageHandler.WIKI_IMAGE_MACRO(image.getFilename(), image.getContentType(), this.thumbnailsAllowed);
            }
            String alternativeText = node.attr("alt");
            String titleText = node.attr("title");
            log.warn("Could not find attachment: '" + alternativeText + "' (" + titleText + ") for content id: " + contentId);
            String text = StringUtils.isNotBlank((CharSequence)alternativeText) ? alternativeText : (StringUtils.isNotBlank((CharSequence)titleText) ? titleText : contentId);
            return text;
        }
        return LinkAndImageHandler.WIKI_MACRO_ATTACHMENT(imageSource);
    }

    private MailUtils.Attachment getAttachmentById(String contentId) {
        for (MailUtils.Attachment attachment : this.attachments) {
            if (!StringUtils.equalsIgnoreCase((CharSequence)attachment.getContentId(), (CharSequence)contentId)) continue;
            return attachment;
        }
        return null;
    }

    private static boolean containsUrlInLinkText(Element element) {
        for (String text : StringUtils.split((String)element.text())) {
            try {
                new URL(StringUtils.trimToEmpty((String)text));
                return true;
            }
            catch (Exception exception) {
            }
        }
        return false;
    }
}

