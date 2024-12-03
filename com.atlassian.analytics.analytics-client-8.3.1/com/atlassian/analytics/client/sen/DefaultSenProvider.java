/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.sen;

import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.sal.api.license.LicenseHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSenProvider
implements SenProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSenProvider.class);
    private final LicenseHandler licenseHandler;
    private String sen;

    public DefaultSenProvider(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    @Override
    public Optional<String> getSen() {
        if (this.sen == null) {
            this.sen = this.getAllSens().stream().filter(StringUtils::isNotBlank).findFirst().orElse(null);
        }
        return Optional.ofNullable(this.sen);
    }

    private Collection<String> getAllSens() {
        try {
            return this.licenseHandler.getAllSupportEntitlementNumbers();
        }
        catch (RuntimeException e) {
            LOG.warn("Couldn't get the SENs for this instance", (Throwable)e);
            return Collections.emptySet();
        }
    }
}

