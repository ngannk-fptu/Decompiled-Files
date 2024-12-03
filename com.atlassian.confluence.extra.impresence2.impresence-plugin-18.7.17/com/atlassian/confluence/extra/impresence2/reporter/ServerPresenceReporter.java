/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.impresence2.reporter.LocaleAwarePresenceReporter;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang.StringUtils;

public abstract class ServerPresenceReporter
extends LocaleAwarePresenceReporter {
    private static final String SERVER_NAME = "extra.im.server.name.";
    protected BandanaManager bandanaManager;

    public ServerPresenceReporter(LocaleSupport localeSupport, @ComponentImport BandanaManager bandanaManager) {
        super(localeSupport);
        this.bandanaManager = bandanaManager;
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public boolean requiresConfig() {
        return StringUtils.isBlank((String)this.getServer());
    }

    public String getServer() {
        return (String)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), SERVER_NAME + this.getKey());
    }

    public void setServer(String server) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), SERVER_NAME + this.getKey(), (Object)server);
    }
}

