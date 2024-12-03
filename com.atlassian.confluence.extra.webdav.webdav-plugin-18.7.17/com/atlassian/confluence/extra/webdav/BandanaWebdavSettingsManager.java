/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.setup.xstream.ConfluenceXStream
 *  com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.webdav.WebdavSettings;
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.xstream.ConfluenceXStream;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WebdavSettingsManager.class})
public class BandanaWebdavSettingsManager
implements WebdavSettingsManager {
    private static final String SETTINGS_KEY = "com.atlassian.confluence.extra.webdav-2.0.settings";
    private final BandanaManager bandanaManager;
    private final ConfluenceXStream pluginXStream;

    @Autowired
    public BandanaWebdavSettingsManager(@ComponentImport BandanaManager bandanaManager, @ComponentImport ConfluenceXStreamManager xStreamManager) {
        this.bandanaManager = bandanaManager;
        this.pluginXStream = xStreamManager.getPluginXStream(this.getClass().getClassLoader());
    }

    @Override
    public void save(WebdavSettings webdavSettings) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SETTINGS_KEY, (Object)webdavSettings);
    }

    @Override
    public WebdavSettings getWebdavSettings() {
        WebdavSettings webdavSettings = null;
        Object setting = this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SETTINGS_KEY);
        if (setting instanceof WebdavSettings) {
            webdavSettings = (WebdavSettings)setting;
        } else if (setting instanceof String) {
            String webdavSettingsXml = (String)setting;
            webdavSettings = StringUtils.isNotBlank((CharSequence)webdavSettingsXml) ? (WebdavSettings)this.pluginXStream.fromXML(webdavSettingsXml) : new WebdavSettings();
        }
        return webdavSettings != null ? webdavSettings : new WebdavSettings();
    }

    @Override
    public boolean isClientInWriteBlacklist(String userAgent) {
        if (null == userAgent) {
            return false;
        }
        for (String regex : this.getWriteBlacklistClients()) {
            if (!Pattern.compile(regex).matcher(userAgent).find()) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<String> getWriteBlacklistClients() {
        return this.getWebdavSettings().getExcludedClientUserAgentRegexes();
    }

    @Override
    public boolean isContentExportsResourceEnabled() {
        return this.getWebdavSettings().isContentExportsResourceEnabled();
    }

    @Override
    public boolean isContentVersionsResourceEnabled() {
        return this.getWebdavSettings().isContentVersionsResourceEnabled();
    }

    @Override
    public boolean isContentUrlResourceEnabled() {
        return this.getWebdavSettings().isContentUrlResourceEnabled();
    }

    @Override
    public boolean isStrictPageResourcePathCheckingDisabled() {
        return this.getWebdavSettings().isStrictPageResourcePathCheckingDisabled();
    }
}

