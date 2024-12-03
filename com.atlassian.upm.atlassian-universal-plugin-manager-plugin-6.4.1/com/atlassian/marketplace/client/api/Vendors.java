/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.api.VendorQuery;
import com.atlassian.marketplace.client.model.Vendor;
import com.atlassian.marketplace.client.model.VendorSummary;
import java.util.Optional;

public interface Vendors {
    public Optional<Vendor> safeGetById(VendorId var1) throws MpacException;

    public Page<VendorSummary> find(VendorQuery var1) throws MpacException;

    public Vendor createVendor(Vendor var1) throws MpacException;

    public Vendor updateVendor(Vendor var1, Vendor var2) throws MpacException;
}

