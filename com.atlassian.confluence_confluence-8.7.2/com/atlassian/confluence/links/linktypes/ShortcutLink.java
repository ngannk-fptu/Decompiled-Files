/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.BaseLink
 *  com.atlassian.renderer.links.GenericLinkParser
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.links.linktypes;

import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.links.BaseLink;
import com.atlassian.renderer.links.GenericLinkParser;
import org.apache.commons.lang3.StringUtils;

public class ShortcutLink
extends BaseLink {
    public static final String SHORTCUT_ICON = "shortcut";

    public ShortcutLink(GenericLinkParser parser, ShortcutLinksManager shortCutLinksManager) {
        super(parser);
        this.iconName = SHORTCUT_ICON;
        String shortcutName = parser.getShortcutName();
        if (shortCutLinksManager.hasShortcutLink(shortcutName)) {
            ShortcutLinkConfig shortcutLinkConfig = shortCutLinksManager.getShortcutLinkConfig(shortcutName);
            String urlTemplate = shortcutLinkConfig.getExpandedValue();
            this.url = HtmlUtil.completeUrlEncode(ShortcutLink.substituteShortcutValue(urlTemplate, parser.getShortcutValue(), true));
            String defaultAlias = shortcutLinkConfig.getDefaultAlias();
            if (StringUtils.isNotEmpty((CharSequence)defaultAlias) && !StringUtils.isNotEmpty((CharSequence)parser.getLinkBody())) {
                this.linkBody = ShortcutLink.substituteShortcutValue(defaultAlias, parser.getShortcutValue(), false);
            }
            this.setI18nTitle("renderer.external.shortcut.link", null);
        }
    }

    protected static String substituteShortcutValue(String template, String shortcutValue, boolean placeAtEndIfMissing) {
        if (template.indexOf("%s") >= 0) {
            return template.replaceAll("%s", shortcutValue);
        }
        if (placeAtEndIfMissing) {
            return template + shortcutValue;
        }
        return template;
    }

    public boolean hasDestination() {
        return StringUtils.isNotEmpty((CharSequence)this.url);
    }
}

