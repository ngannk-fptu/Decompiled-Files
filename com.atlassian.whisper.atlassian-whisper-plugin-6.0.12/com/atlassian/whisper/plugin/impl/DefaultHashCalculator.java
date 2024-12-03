/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.client.ServerIdProvider
 *  com.atlassian.analytics.client.logger.EventAnonymizer
 *  com.atlassian.analytics.client.uuid.ProductUUIDProvider
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.whisper.plugin.api.HashCalculator
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.analytics.client.ServerIdProvider;
import com.atlassian.analytics.client.logger.EventAnonymizer;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.whisper.plugin.api.HashCalculator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ExportAsService
public class DefaultHashCalculator
implements HashCalculator {
    private final EventAnonymizer eventAnonymizer;
    private final ApplicationProperties applicationProperties;
    private final AtomicReference<String> server;

    @Inject
    public DefaultHashCalculator(@ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport LicenseHandler licenseHandler, @ComponentImport ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.eventAnonymizer = new EventAnonymizer(new ProductUUIDProvider(pluginSettingsFactory, new ServerIdProvider(licenseHandler)));
        this.server = new AtomicReference();
    }

    public String calculateUserHash(String userName) {
        return this.eventAnonymizer.hash(userName);
    }

    public String calculateInstanceHash() {
        String result = this.server.get();
        if (result != null) {
            return result;
        }
        String baseUrl = this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
        try {
            result = this.calculateUserHash(new URL(baseUrl).getHost());
            this.server.set(result);
            return result;
        }
        catch (MalformedURLException e) {
            return "-";
        }
    }
}

