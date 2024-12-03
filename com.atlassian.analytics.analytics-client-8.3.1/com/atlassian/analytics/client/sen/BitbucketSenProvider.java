/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.server.ApplicationMode
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 *  com.atlassian.sal.api.license.LicenseHandler
 *  io.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.analytics.client.sen;

import com.atlassian.analytics.client.configuration.BitbucketAnalyticsSettings;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.sal.api.license.LicenseHandler;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class BitbucketSenProvider
implements SenProvider {
    private final ApplicationPropertiesService applicationPropertiesService;
    private final BitbucketAnalyticsSettings bitbucketAnalyticsSettings;
    private final LicenseHandler licenseHandler;
    private final LazyReference<ApplicationMode> applicationMode = new LazyReference<ApplicationMode>(){

        protected ApplicationMode create() {
            return BitbucketSenProvider.this.applicationPropertiesService.getMode();
        }
    };

    public BitbucketSenProvider(ApplicationPropertiesService applicationPropertiesService, BitbucketAnalyticsSettings bitbucketAnalyticsSettings, LicenseHandler licenseHandler) {
        this.applicationPropertiesService = Objects.requireNonNull(applicationPropertiesService);
        this.bitbucketAnalyticsSettings = Objects.requireNonNull(bitbucketAnalyticsSettings);
        this.licenseHandler = Objects.requireNonNull(licenseHandler);
    }

    @Override
    public Optional<String> getSen() {
        String sen;
        if (this.applicationMode.get() == ApplicationMode.MIRROR && StringUtils.isNotBlank((CharSequence)(sen = this.bitbucketAnalyticsSettings.getSupportEntitlementNumber()))) {
            return Optional.of(sen);
        }
        return Optional.ofNullable(this.licenseHandler.getSupportEntitlementNumber());
    }
}

