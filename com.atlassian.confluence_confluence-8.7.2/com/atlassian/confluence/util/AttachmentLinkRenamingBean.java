/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.util.LinkRenamingBean;
import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;

public class AttachmentLinkRenamingBean
implements LinkRenamingBean {
    private String renamedContent;
    private static final String ALIAS_PATTERN = "([^\\|\\[\\]]*\\| *)?";
    private static final String ATTACHMENT_PATTERN = "(\\^[^\\[\\]]*)";

    public AttachmentLinkRenamingBean(ContentEntityObject referringContent, String oldFileName, String newFileName) {
        String normalLinkPattern = "\\^{0}]";
        String normalLinkSubstitute = "\\^{0}]";
        String oldString = MessageFormat.format("\\^{0}]", this.regexQuote(oldFileName));
        String newString = MessageFormat.format("\\^{0}]", this.escapeDollars(newFileName));
        this.renamedContent = referringContent.getBodyContent().getBody().replaceAll(oldString, newString);
    }

    public AttachmentLinkRenamingBean(ContentEntityObject referringContent, ContentEntityObject oldLocation, String newSpaceKey, String newPageTitle) {
        StringBuffer actualLink = new StringBuffer();
        actualLink.append("\\[").append(ALIAS_PATTERN);
        this.appendSpaceKeyPattern(actualLink, oldLocation);
        this.appendPageTitlePattern(actualLink, referringContent, oldLocation);
        actualLink.append(ATTACHMENT_PATTERN).append("\\]");
        String replaceLink = "[$1" + this.escapeDollars(this.getReplacingLinkPageTitle(referringContent, newSpaceKey, newPageTitle)) + "$4]";
        this.renamedContent = referringContent.getBodyContent().getBody().replaceAll(actualLink.toString(), replaceLink);
    }

    public AttachmentLinkRenamingBean(ContentEntityObject referringContent, String oldFileName, String newFileName, ContentEntityObject oldLocation, ContentEntityObject newLocation) {
        String destSpaceKey = ((SpaceContentEntityObject)newLocation).getSpaceKey();
        String destPageTitle = newLocation.getTitle();
        StringBuffer actualLink = new StringBuffer();
        actualLink.append("\\[").append(ALIAS_PATTERN);
        this.appendSpaceKeyPattern(actualLink, oldLocation);
        this.appendPageTitlePattern(actualLink, referringContent, oldLocation);
        actualLink.append("\\^").append(this.regexQuote(oldFileName)).append("\\]");
        String replaceLink = "[$1" + this.escapeDollars(this.getReplacingLinkPageTitle(referringContent, destSpaceKey, destPageTitle)) + "\\^" + this.escapeDollars(newFileName) + "\\]";
        this.renamedContent = referringContent.getBodyContent().getBody().replaceAll(actualLink.toString(), replaceLink);
    }

    private void appendPageTitlePattern(StringBuffer buffer, ContentEntityObject referringContent, ContentEntityObject destContent) {
        buffer.append("(").append(this.regexQuote(destContent.getTitle())).append(")");
        if (referringContent.equals(destContent)) {
            buffer.append("?");
        }
    }

    private String getReplacingLinkPageTitle(ContentEntityObject referringContent, String spaceKey, String pageTitle) {
        if (referringContent instanceof SpaceContentEntityObject && !((SpaceContentEntityObject)referringContent).getSpaceKey().equals(spaceKey)) {
            return spaceKey + ":" + pageTitle;
        }
        return StringUtils.equals((CharSequence)referringContent.getTitle(), (CharSequence)pageTitle) ? "" : pageTitle;
    }

    private void appendSpaceKeyPattern(StringBuffer buffer, ContentEntityObject sourceContent) {
        String spaceKey = ((SpaceContentEntityObject)sourceContent).getSpaceKey();
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            buffer.append("(").append(spaceKey).append(":)?");
        }
    }

    private String regexQuote(String s) {
        return "\\Q" + s + "\\E";
    }

    protected String escapeDollars(String s) {
        StringBuilder buf = new StringBuilder(s.length() + 5);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '$') {
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }

    @Override
    public String getRenamedContent() {
        return this.renamedContent;
    }
}

