/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.user.LicenceCheckResultStatus;
import lombok.Generated;

public class LicenceCheckResult {
    private String product;
    private String edition;
    private int usersCount;
    private LicenceCheckResultStatus result;
    private int availableLicenceSeats;
    private int requestedLicenceSeats;

    @Generated
    public LicenceCheckResult(String product, String edition, int usersCount, LicenceCheckResultStatus result, int availableLicenceSeats, int requestedLicenceSeats) {
        this.product = product;
        this.edition = edition;
        this.usersCount = usersCount;
        this.result = result;
        this.availableLicenceSeats = availableLicenceSeats;
        this.requestedLicenceSeats = requestedLicenceSeats;
    }

    @Generated
    public String getProduct() {
        return this.product;
    }

    @Generated
    public String getEdition() {
        return this.edition;
    }

    @Generated
    public int getUsersCount() {
        return this.usersCount;
    }

    @Generated
    public LicenceCheckResultStatus getResult() {
        return this.result;
    }

    @Generated
    public int getAvailableLicenceSeats() {
        return this.availableLicenceSeats;
    }

    @Generated
    public int getRequestedLicenceSeats() {
        return this.requestedLicenceSeats;
    }
}

