/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import java.util.Collections;
import java.util.Map;

public class ConfluenceLicenseDetails {
    private final Integer numberOfUsers;

    public ConfluenceLicenseDetails(Integer numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public static ConfluenceLicenseDetails from(SingleProductLicenseDetailsView singleProductLicenseDetailsView) {
        return new ConfluenceLicenseDetails(singleProductLicenseDetailsView.getNumberOfUsers());
    }

    public Map<String, Integer> toMigrationProperties() {
        if (this.numberOfUsers != null) {
            return Collections.singletonMap("CONFLUENCE", this.numberOfUsers);
        }
        return Collections.emptyMap();
    }
}

