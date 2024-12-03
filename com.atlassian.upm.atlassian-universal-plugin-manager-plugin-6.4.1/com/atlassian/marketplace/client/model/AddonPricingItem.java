/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.LicenseEditionType;
import com.atlassian.marketplace.client.model.ReadOnly;
import io.atlassian.fugue.Option;

public class AddonPricingItem {
    String description;
    String editionId;
    @ReadOnly
    String editionDescription;
    LicenseEditionType editionType;
    String licenseType;
    float amount;
    @ReadOnly
    Option<Float> renewalAmount;
    int unitCount;
    int monthsValid;

    public String getDescription() {
        return this.description;
    }

    public String getEditionId() {
        return this.editionId;
    }

    public String getEditionDescription() {
        return this.editionDescription;
    }

    public LicenseEditionType getEditionType() {
        return this.editionType;
    }

    public String getLicenseType() {
        return this.licenseType;
    }

    public float getAmount() {
        return this.amount;
    }

    public Option<Float> getRenewalAmount() {
        return this.renewalAmount;
    }

    public int getUnitCount() {
        return this.unitCount;
    }

    public int getMonthsValid() {
        return this.monthsValid;
    }
}

