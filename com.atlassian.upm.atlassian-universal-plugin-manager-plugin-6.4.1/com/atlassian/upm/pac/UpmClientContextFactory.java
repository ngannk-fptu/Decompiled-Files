/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.upm.pac;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.pac.BaseClientContextFactory;
import com.atlassian.upm.core.pac.ClientContext;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import java.util.Iterator;
import java.util.Objects;

public class UpmClientContextFactory
extends BaseClientContextFactory {
    private static final String DEFAULT_DEV_MODE_SEN = "TEST_SEN";
    private final HostApplicationDescriptor hostApplicationDescriptor;
    private final HostLicenseProvider hostLicenseProvider;
    private final UpmHostApplicationInformation appInfo;

    public UpmClientContextFactory(ApplicationProperties applicationProperties, HostApplicationDescriptor hostApplicationDescriptor, HostLicenseProvider hostLicenseProvider, UpmHostApplicationInformation appInfo) {
        super(applicationProperties, appInfo);
        this.hostApplicationDescriptor = Objects.requireNonNull(hostApplicationDescriptor, "hostApplicationDescriptor");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
    }

    @Override
    protected String getClientType() {
        return "upm";
    }

    @Override
    protected ClientContext.Builder createContext(boolean forceServerDataCollection) {
        ClientContext.Builder builder = super.createContext(forceServerDataCollection);
        if (UpmSys.isAnalyticsConfiguredToSendServerInformation() || forceServerDataCollection) {
            String sen = null;
            HostApplicationLicenseAttributes attrs = this.hostLicenseProvider.getHostApplicationLicenseAttributes();
            Iterator<String> iterator = attrs.getSen().iterator();
            while (iterator.hasNext()) {
                String licSen;
                sen = licSen = iterator.next();
            }
            String serverId = this.appInfo.getServerId();
            boolean evaluation = attrs.isEvaluation();
            if (sen == null && Sys.isUpmDebugModeEnabled()) {
                sen = DEFAULT_DEV_MODE_SEN;
            }
            builder.productEvaluation(evaluation).sen(sen).serverId(serverId).userCount(this.hostApplicationDescriptor.getActiveEditionCount());
        }
        return builder;
    }
}

