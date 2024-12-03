/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.TopVendorProgram;
import io.atlassian.fugue.Option;

public final class VendorPrograms {
    Option<TopVendorProgram> topVendor = Option.none();

    public Option<TopVendorProgram> getTopVendor() {
        return this.topVendor;
    }
}

