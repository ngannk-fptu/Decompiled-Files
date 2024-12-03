/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fx3.Fx3Client
 *  com.atlassian.fx3.httpclient.HttpCallback
 *  com.atlassian.fx3.setup.Fx3Config
 *  com.atlassian.sal.api.license.LicenseHandler
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.featureflag;

import com.atlassian.fx3.Fx3Client;
import com.atlassian.fx3.httpclient.HttpCallback;
import com.atlassian.fx3.setup.Fx3Config;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.featureflag.Fx3OkhttpAdapter;
import com.atlassian.sal.api.license.LicenseHandler;
import java.util.HashMap;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fx3ClientFactory {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(Fx3ClientFactory.class);
    private final Fx3OkhttpAdapter fx3OkhttpAdapter;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final LicenseHandler licenseHandler;

    public Fx3ClientFactory(Fx3OkhttpAdapter fx3OkhttpAdapter, MigrationAgentConfiguration migrationAgentConfiguration, LicenseHandler licenseHandler) {
        this.fx3OkhttpAdapter = fx3OkhttpAdapter;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.licenseHandler = licenseHandler;
    }

    public Fx3Client create() {
        try {
            Fx3Config fx3Config = new Fx3Config(this.migrationAgentConfiguration.getFx3baseUrl(), this.migrationAgentConfiguration.getFx3EnvironmentKey());
            HashMap<String, String> customAttributes = new HashMap<String, String>();
            customAttributes.put("ServerId", this.licenseHandler.getServerId());
            return Fx3Client.Companion.init(fx3Config, customAttributes, (HttpCallback)this.fx3OkhttpAdapter);
        }
        catch (Exception e) {
            log.error("Error occurred while initializing Fx3Client and only defaults will be used.", (Throwable)e);
            return null;
        }
    }
}

