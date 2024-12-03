/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.WikiMarkupParser
 *  com.atlassian.renderer.v2.components.MacroTag
 *  com.atlassian.renderer.v2.components.WikiContentHandler
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.util.LinkRenamingBean;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractLinkRenamingBean
implements LinkRenamingBean,
WikiContentHandler {
    protected SpaceContentEntityObject pageBeingChanged;
    protected String newSpaceKey;
    protected String newTitle;
    protected ContentEntityObject referringContent;
    private MacroManager macroManager;
    private WikiMarkupParser parser;

    public AbstractLinkRenamingBean(ContentEntityObject referringContent, SpaceContentEntityObject pageBeingChanged, String newSpaceKey, String newTitle) {
        this.pageBeingChanged = pageBeingChanged;
        this.newSpaceKey = newSpaceKey;
        this.newTitle = newTitle;
        this.referringContent = referringContent;
    }

    protected String getReferringContentSpaceKey() {
        if (this.referringContent instanceof SpaceContentEntityObject) {
            return ((SpaceContentEntityObject)this.referringContent).getSpaceKey();
        }
        return "";
    }

    protected SpaceContentEntityObject getPageBeingChanged() {
        return this.pageBeingChanged;
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
        return this.parseContent(this.referringContent.getBodyAsString());
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
        assert (macroTag.command != null);
        Macro macro = this.macroManager.getEnabledMacro(macroTag.command.toLowerCase());
        buffer.append('{').append(macroTag.command);
        if (StringUtils.isNotEmpty((CharSequence)macroTag.argString)) {
            buffer.append(':').append(macroTag.argString);
        }
        String newBody = macro != null && macro.hasBody() && (macro.getBodyRenderMode() == null || macro.getBodyRenderMode().renderLinks()) ? this.parseContent(body) : body;
        buffer.append('}').append(newBody);
        if (macroTag.getEndTag() != null) {
            buffer.append('{').append(macroTag.command).append('}');
        }
    }

    public abstract void handleText(StringBuffer var1, String var2);

    private String parseContent(String content) {
        if (this.parser == null) {
            this.parser = new WikiMarkupParser(this.macroManager, (WikiContentHandler)this);
        }
        return this.parser.parse(content);
    }
}

