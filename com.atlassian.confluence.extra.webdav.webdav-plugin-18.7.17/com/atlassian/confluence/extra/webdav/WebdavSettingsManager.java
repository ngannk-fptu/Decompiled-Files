/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.WebdavSettings;
import java.util.Set;

public interface WebdavSettingsManager {
    public WebdavSettings getWebdavSettings();

    public boolean isClientInWriteBlacklist(String var1);

    public Set<String> getWriteBlacklistClients();

    public boolean isContentExportsResourceEnabled();

    public boolean isContentVersionsResourceEnabled();

    public boolean isContentUrlResourceEnabled();

    public boolean isStrictPageResourcePathCheckingDisabled();

    public void save(WebdavSettings var1);
}

