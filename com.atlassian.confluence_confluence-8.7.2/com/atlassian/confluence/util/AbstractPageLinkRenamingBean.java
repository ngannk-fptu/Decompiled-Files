/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.util.AbstractLinkRenamingBean;
import java.text.MessageFormat;

public abstract class AbstractPageLinkRenamingBean
extends AbstractLinkRenamingBean {
    public AbstractPageLinkRenamingBean(ContentEntityObject referringContent, SpaceContentEntityObject pageBeingChanged, String newSpaceKey, String newTitle) {
        super(referringContent, pageBeingChanged, newSpaceKey, newTitle);
    }

    @Override
    public void handleText(StringBuffer buffer, String s) {
        String aliasPart = "([^\\[\\]]*\\| *)?";
        String anchorPart = "(#[^\\]]*)?";
        String normalLinkPattern = "\\[" + aliasPart + "{0}" + anchorPart + "\\]";
        String normalLinkSubstitute = "\\[$1\\{0}$2\\]";
        String oldString = MessageFormat.format(normalLinkPattern, this.getCurrentLinkPart());
        String newString = MessageFormat.format(normalLinkSubstitute, this.escapeDollars(this.getNewLinkPart()));
        buffer.append(s.replaceAll(oldString, newString));
    }

    protected abstract String getCurrentLinkPart();

    protected abstract String getNewLinkPart();
}

