/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.util.AbstractLinkRenamingBean;
import com.atlassian.confluence.util.LinkRenamingBean;
import java.text.MessageFormat;

public class SelfPageLinkRenamingBean
extends AbstractLinkRenamingBean
implements LinkRenamingBean {
    private String newSpaceKey;
    private ContentEntityObject contentBeingMoved;

    public SelfPageLinkRenamingBean(ContentEntityObject contentBeingMoved, String newSpaceKey) {
        super(contentBeingMoved, (SpaceContentEntityObject)contentBeingMoved, newSpaceKey, null);
        this.newSpaceKey = newSpaceKey;
        this.contentBeingMoved = contentBeingMoved;
    }

    @Override
    public void handleText(StringBuffer buffer, String s) {
        String selfLinkPattern = "\\[([^\\|\\[\\]]*\\| *)?(?:{0}:)?{1}(#[^\\]]*)?\\]";
        String selfLinkSubstitute = "\\[$1{0}$2\\]";
        String title = this.escapeRegularExpression(this.contentBeingMoved.getTitle());
        String oldString = MessageFormat.format(selfLinkPattern, this.getCurrentSpaceKey(), title);
        String newString = MessageFormat.format(selfLinkSubstitute, this.newSpaceKey + ":" + title);
        String currentContent = s.replaceAll(oldString, newString);
        String normalLinkPattern = "\\[([^\\|\\[\\]]*\\| *)?{0}(#[^\\]]*)?\\]";
        String normalLinkSubstitute = "\\[$1{0}$3\\]";
        oldString = MessageFormat.format(normalLinkPattern, this.getCurrentTitle());
        newString = MessageFormat.format(normalLinkSubstitute, this.getNewTitle());
        currentContent = currentContent.replaceAll(oldString, newString);
        oldString = MessageFormat.format(normalLinkPattern, this.getTitleRegExpForKey(this.newSpaceKey));
        newString = MessageFormat.format(normalLinkSubstitute, "$2");
        buffer.append(currentContent.replaceAll(oldString, newString));
    }

    private String escapeRegularExpression(String s) {
        String[] regexChars;
        for (String regexChar : regexChars = new String[]{"\\", "[", "]", "|", "^", "$", "*", "(", ")", "+", "?", "."}) {
            s = s.replaceAll("\\" + regexChar, "\\\\\\" + regexChar);
        }
        return s;
    }

    private String getCurrentTitle() {
        return this.getTitleRegExpForKey(this.getCurrentSpaceKey());
    }

    private String getTitleRegExpForKey(String key) {
        return "(?:" + key + ":)?([^~\\^/#][^\\[\\]:]+)";
    }

    private String getNewTitle() {
        if (this.getCurrentSpaceKey().equals(this.newSpaceKey)) {
            return "$2";
        }
        return this.getCurrentSpaceKey() + ":$2";
    }

    private String getCurrentSpaceKey() {
        return ((SpaceContentEntityObject)this.contentBeingMoved).getSpaceKey();
    }
}

