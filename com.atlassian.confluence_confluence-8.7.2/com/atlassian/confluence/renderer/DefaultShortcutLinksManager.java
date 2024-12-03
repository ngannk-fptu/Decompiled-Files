/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.core.util.PropertyUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.renderer;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.util.PropertyUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultShortcutLinksManager
implements ShortcutLinksManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultShortcutLinksManager.class);
    public static final String DEFAULT_PROPERTIES = "default-shortcut-map.properties";
    BandanaManager bandanaManager;

    @Override
    public Map<String, ShortcutLinkConfig> getShortcutLinks() {
        Map shortcutLinks = (Map)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.shortcut.links");
        if (shortcutLinks == null) {
            shortcutLinks = this.getDefaults();
        }
        return shortcutLinks;
    }

    @Override
    public void addShortcutLink(String key, ShortcutLinkConfig shortcutLinkConfig) {
        Map<String, ShortcutLinkConfig> shortcutLinks = this.getShortcutLinks();
        shortcutLinks.put(key.toLowerCase(), shortcutLinkConfig);
        this.updateShortcutLinks(shortcutLinks);
    }

    @Override
    public void removeShortcutLink(String key) {
        Map<String, ShortcutLinkConfig> shortcutLinks = this.getShortcutLinks();
        shortcutLinks.remove(key);
        this.updateShortcutLinks(shortcutLinks);
    }

    @Override
    public void updateShortcutLinks(Map shortcutLinks) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.shortcut.links", (Object)shortcutLinks);
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public boolean hasShortcutLink(String key) {
        if (StringUtils.isNotEmpty((CharSequence)key)) {
            return this.getShortcutLinks().containsKey(key.toLowerCase());
        }
        return false;
    }

    @Override
    public ShortcutLinkConfig getShortcutLinkConfig(String key) {
        ShortcutLinkConfig o = this.getShortcutLinks().get(key.toLowerCase());
        if (o instanceof ShortcutLinkConfig) {
            ShortcutLinkConfig shortcutLinkConfig = o;
            return shortcutLinkConfig;
        }
        if (o instanceof String) {
            ShortcutLinkConfig shortcutLinkConfig = new ShortcutLinkConfig();
            shortcutLinkConfig.setExpandedValue((String)((Object)o));
            return shortcutLinkConfig;
        }
        if (o == null) {
            return null;
        }
        log.warn("Object of type " + o.getClass() + " found instead of a ShortcutLinkConfig for key: " + key);
        return null;
    }

    @Override
    public String resolveShortcutUrl(String key, String parameter) {
        if (!this.hasShortcutLink(key)) {
            throw new IllegalArgumentException("No shortcut found with key: " + key);
        }
        ShortcutLinkConfig config = this.getShortcutLinkConfig(key);
        String expandedValue = config.getExpandedValue();
        Object url = expandedValue.contains("%s") ? expandedValue.replaceAll("%s", parameter) : expandedValue + parameter;
        return HtmlUtil.completeUrlEncode((String)url);
    }

    @Override
    public String resolveDefaultLinkAlias(String key, String parameter) {
        if (!this.hasShortcutLink(key)) {
            throw new IllegalArgumentException("No shortcut found with key: " + key);
        }
        ShortcutLinkConfig config = this.getShortcutLinkConfig(key);
        Object alias = StringUtils.defaultString((String)config.getDefaultAlias());
        if (((String)alias).contains("%s")) {
            alias = ((String)alias).replaceAll("%s", parameter);
        }
        if (StringUtils.isBlank((CharSequence)alias)) {
            alias = parameter + "@" + key;
        }
        return HtmlUtil.htmlEncode((String)alias);
    }

    private Map getDefaults() {
        HashMap<Object, ShortcutLinkConfig> result = new HashMap<Object, ShortcutLinkConfig>();
        Properties defaultProperties = PropertyUtils.getProperties((String)DEFAULT_PROPERTIES, this.getClass());
        for (Map.Entry<Object, Object> entry : defaultProperties.entrySet()) {
            ShortcutLinkConfig linkConfig = new ShortcutLinkConfig();
            linkConfig.setExpandedValue((String)entry.getValue());
            result.put(entry.getKey(), linkConfig);
        }
        return result;
    }
}

