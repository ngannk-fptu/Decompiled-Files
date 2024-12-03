/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerInfoAppender
extends RootLevelSupportDataAppender {
    private static final String UNKNOWN = "unknown";
    private static final String ATST_VERSION = "stp.properties.atst.version";
    private static final String PRODUCT = "stp.properties.product";
    private static final String PRODUCT_NAME = "stp.properties.product.name";
    private static final String PRODUCT_VERSION = "stp.properties.product.version";
    private static final String APPLICATION_SEN = "stp.properties.sen";
    private static final String SERVER_ID = "stp.properties.server.id";
    private static final String PRODUCT_LICENSE_TYPE = "stp.properties.product.license-type";
    private final SupportApplicationInfo info;
    private final LicenseService licenseService;

    @Autowired
    public ServerInfoAppender(SupportApplicationInfo info, LicenseService licenseService) {
        this.info = info;
        this.licenseService = licenseService;
    }

    @Override
    public void addSupportData(SupportDataBuilder builder) {
        this.addAtstVersion(builder);
        this.addProductInfo(builder);
        this.addTimeZone(builder);
        this.addSen(builder);
        this.addServerId(builder);
    }

    private void addAtstVersion(SupportDataBuilder builder) {
        String atstVersion = this.info.getStpVersion();
        builder.addValue(ATST_VERSION, atstVersion != null ? atstVersion : UNKNOWN);
    }

    private void addProductInfo(SupportDataBuilder builder) {
        SupportDataBuilder productBuilder = builder.addCategory(PRODUCT);
        productBuilder.addValue(PRODUCT_NAME, this.info.getApplicationName());
        productBuilder.addValue(PRODUCT_VERSION, this.info.getApplicationVersion());
        try {
            productBuilder.addValue(PRODUCT_LICENSE_TYPE, this.licenseService.isLicensedForDataCenter() ? "data-center" : "server");
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
    }

    private void addTimeZone(SupportDataBuilder builder) {
        builder.addValue("stp.properties.timezone", this.info.getTimeZoneRelativeToGMT());
    }

    private void addSen(SupportDataBuilder builder) {
        String applicationSEN = this.info.getApplicationSEN();
        builder.addValue(APPLICATION_SEN, applicationSEN != null ? applicationSEN : UNKNOWN);
    }

    private void addServerId(SupportDataBuilder builder) {
        String serverId = this.info.getApplicationServerID();
        builder.addValue(SERVER_ID, serverId != null ? serverId : UNKNOWN);
    }
}

