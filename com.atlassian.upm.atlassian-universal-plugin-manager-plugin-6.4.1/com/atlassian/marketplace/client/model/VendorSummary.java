/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.VendorBase;
import io.atlassian.fugue.Option;

public final class VendorSummary
extends VendorBase {
    Embedded _embedded;

    @Override
    public Option<ImageInfo> getLogo() {
        return this._embedded.logo;
    }

    static final class Embedded {
        Option<ImageInfo> logo;

        Embedded() {
        }
    }
}

