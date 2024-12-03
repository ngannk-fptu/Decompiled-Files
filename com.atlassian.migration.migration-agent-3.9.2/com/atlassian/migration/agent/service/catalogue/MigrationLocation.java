/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.MigrationLocationType;
import com.google.gson.annotations.SerializedName;

public class MigrationLocation {
    @SerializedName(value="_type")
    private final MigrationLocationType type;
    private final String productFamily;
    private final String url;
    private static final String PRODUCT_FAMILY = "CONFLUENCE";

    public MigrationLocation(MigrationLocationType type, String url) {
        this.type = type;
        this.productFamily = PRODUCT_FAMILY;
        this.url = url;
    }

    public MigrationLocationType getType() {
        return this.type;
    }

    public String getProductFamily() {
        return this.productFamily;
    }

    public String getUrl() {
        return this.url;
    }
}

