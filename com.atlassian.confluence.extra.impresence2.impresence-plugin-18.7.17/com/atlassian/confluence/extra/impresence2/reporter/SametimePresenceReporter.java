/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.impresence2.reporter.PresenceException;
import com.atlassian.confluence.extra.impresence2.reporter.ServerPresenceReporter;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import java.util.Map;

public class SametimePresenceReporter
extends ServerPresenceReporter {
    public static final String KEY = "sametime";
    private final VelocityHelperService velocityHelperService;

    public SametimePresenceReporter(LocaleSupport localeSupport, @ComponentImport BandanaManager bandanaManager, @ComponentImport VelocityHelperService velocityHelperService) {
        super(localeSupport, bandanaManager);
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getName() {
        return this.getText("presencereporter.sametime.name");
    }

    @Override
    public String getServiceHomepage() {
        return this.getText("presencereporter.sametime.servicehomepage");
    }

    @Override
    public String getPresenceXHTML(String id, boolean outputId) throws IOException, PresenceException {
        Map<String, Object> velocityContext = this.getMacroDefaultVelocityContext();
        velocityContext.put("user", id);
        velocityContext.put("server", this.getServer());
        velocityContext.put("outputId", outputId);
        return this.generateOutputFromVelocity(velocityContext);
    }

    protected String generateOutputFromVelocity(Map<String, Object> velocityContext) {
        return this.velocityHelperService.getRenderedTemplate("templates/extra/impresence2/sametime-presence.vm", velocityContext);
    }

    protected Map<String, Object> getMacroDefaultVelocityContext() {
        return this.velocityHelperService.createDefaultVelocityContext();
    }
}

