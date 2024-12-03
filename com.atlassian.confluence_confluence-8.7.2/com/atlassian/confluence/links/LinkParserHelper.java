/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.WikiMarkupParser
 *  com.atlassian.renderer.v2.components.MacroTag
 *  com.atlassian.renderer.v2.components.WikiContentHandler
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroManager
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.links.linktypes.IncludePageMacroLink;
import com.atlassian.confluence.renderer.radeox.filters.UrlFilter;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.RegexUtils;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import java.util.ArrayList;
import java.util.List;

public class LinkParserHelper
implements WikiContentHandler {
    private final List<String> links = new ArrayList<String>();
    private final String pageContent;
    private final MacroManager macroManager;
    private final SettingsManager settingsManager;

    public LinkParserHelper(String pageContent, MacroManager macroManager, SettingsManager settingsManager) {
        this.macroManager = macroManager;
        this.pageContent = pageContent;
        this.settingsManager = settingsManager;
    }

    public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
        assert (macroTag.command != null);
        Macro macro = this.macroManager.getEnabledMacro(macroTag.command.toLowerCase());
        if (macro != null && macro.hasBody() && (macro.getBodyRenderMode() == null || macro.getBodyRenderMode().renderLinks())) {
            this.extractLinks(body);
        }
    }

    public void handleText(StringBuffer buffer, String s) {
        this.links.addAll(RegexUtils.getMatches("(\\[)([\\p{L}&[^\\[\\]\\p{Space}]][\\p{L}&[^\\[\\]]]*)\\]", s));
        this.links.addAll(RegexUtils.getMatches(UrlFilter.URL_PATTERN, s));
        if (this.settingsManager.getGlobalSettings().isAllowCamelCase()) {
            this.links.addAll(RegexUtils.getMatches("([^a-zA-Z0-9!/\\[]|^)([A-Z])([a-z]+([A-Z][a-zA-Z0-9]+)+)(([^a-zA-Z0-9!\\]])|\r?\n|$)", s));
        }
        this.links.addAll(RegexUtils.getMatches(IncludePageMacroLink.pattern, s));
    }

    public List<String> extractLinks() {
        this.extractLinks(this.pageContent);
        return this.links;
    }

    private void extractLinks(String content) {
        WikiMarkupParser parser = new WikiMarkupParser(this.macroManager, (WikiContentHandler)this);
        parser.parse(content);
    }
}

