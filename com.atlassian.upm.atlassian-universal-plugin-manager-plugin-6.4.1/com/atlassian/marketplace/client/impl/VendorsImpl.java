/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.api.VendorQuery;
import com.atlassian.marketplace.client.api.Vendors;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.ApiImplBase;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.Vendor;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.atlassian.marketplace.client.util.UriBuilder;
import java.util.Optional;

final class VendorsImpl
extends ApiImplBase
implements Vendors {
    VendorsImpl(ApiHelper apiHelper, InternalModel.MinimalLinks root) throws MpacException {
        super(apiHelper, root, "vendors");
    }

    @Override
    public Optional<Vendor> safeGetById(VendorId id) throws MpacException {
        return this.apiHelper.safeGetOptionalEntity(id.getUri(), Vendor.class);
    }

    @Override
    public Vendor createVendor(Vendor vendor) throws MpacException {
        return this.genericCreate(this.getApiRoot(), vendor, Optional.empty());
    }

    @Override
    public Vendor updateVendor(Vendor original, Vendor updated) throws MpacException {
        return this.genericUpdate(original.getSelfUri(), original, updated);
    }

    @Override
    public Page<VendorSummary> find(VendorQuery query) throws MpacException {
        UriBuilder uri = this.fromApiRoot();
        ApiHelper.addVendorQueryParams(query, uri);
        return this.apiHelper.getMore(new PageReference(uri.build(), query.getBounds(), this.pageReader(InternalModel.Vendors.class)));
    }
}

