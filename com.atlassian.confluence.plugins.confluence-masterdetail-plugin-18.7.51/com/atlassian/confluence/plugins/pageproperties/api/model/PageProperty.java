/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.pageproperties.api.model;

import java.io.Serializable;

public class PageProperty
implements Serializable {
    private final String detailStorageFormat;
    private final String headingStorageFormat;

    public PageProperty(String detailStorageFormat, String headingStorageFormat) {
        this.detailStorageFormat = detailStorageFormat;
        this.headingStorageFormat = headingStorageFormat;
    }

    public String getDetailStorageFormat() {
        return this.detailStorageFormat;
    }

    public String getHeadingStorageFormat() {
        return this.headingStorageFormat;
    }
}

