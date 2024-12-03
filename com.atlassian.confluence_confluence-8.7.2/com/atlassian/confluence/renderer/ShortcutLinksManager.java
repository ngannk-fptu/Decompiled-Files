/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import java.util.Map;

public interface ShortcutLinksManager {
    public Map<String, ShortcutLinkConfig> getShortcutLinks();

    public void updateShortcutLinks(Map var1);

    public void addShortcutLink(String var1, ShortcutLinkConfig var2);

    public void removeShortcutLink(String var1);

    public boolean hasShortcutLink(String var1);

    public ShortcutLinkConfig getShortcutLinkConfig(String var1);

    public String resolveShortcutUrl(String var1, String var2);

    public String resolveDefaultLinkAlias(String var1, String var2);
}

