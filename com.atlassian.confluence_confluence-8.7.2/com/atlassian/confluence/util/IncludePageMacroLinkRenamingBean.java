/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.components.MacroTag
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.util.AbstractLinkRenamingBean;
import com.atlassian.renderer.v2.components.MacroTag;
import org.apache.commons.lang3.StringUtils;

public class IncludePageMacroLinkRenamingBean
extends AbstractLinkRenamingBean {
    public IncludePageMacroLinkRenamingBean(ContentEntityObject referringContent, SpaceContentEntityObject pageBeingChanged, String newSpaceKey, String newTitle) {
        super(referringContent, pageBeingChanged, newSpaceKey, newTitle);
    }

    @Override
    public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
        if ("include".equals(macroTag.command)) {
            assert (macroTag.getEndTag() == null);
            buffer.append('{').append(macroTag.command);
            if (StringUtils.isNotEmpty((CharSequence)macroTag.argString)) {
                buffer.append(':');
                buffer.append(macroTag.argString.replaceAll("\\Q" + this.pageBeingChanged.getTitle() + "\\E", this.escapeDollars(this.newTitle)));
            }
            buffer.append('}');
        } else {
            super.handleMacro(buffer, macroTag, body);
        }
    }

    @Override
    public void handleText(StringBuffer buffer, String s) {
        buffer.append(s);
    }
}

