/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.LicenseTypes;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.stream.StreamSupport;

final class LicenseTypesImpl
implements LicenseTypes {
    private final ApiHelper apiHelper;
    private final InternalModel.MinimalLinks root;

    LicenseTypesImpl(ApiHelper apiHelper, InternalModel.MinimalLinks root) {
        this.apiHelper = apiHelper;
        this.root = root;
    }

    @Override
    public Iterable<LicenseType> getAllLicenseTypes() throws MpacException {
        UriBuilder uri = this.licenseTypesBaseUri();
        InternalModel.LicenseTypes collectionRep = this.apiHelper.getEntity(uri.build(), InternalModel.LicenseTypes.class);
        return ImmutableList.copyOf(collectionRep.getItems());
    }

    @Override
    public Optional<LicenseType> safeGetByKey(String licenseTypeKey) throws MpacException {
        return StreamSupport.stream(this.getAllLicenseTypes().spliterator(), false).filter(l -> l.getKey().equals(licenseTypeKey)).findFirst();
    }

    private UriBuilder licenseTypesBaseUri() throws MpacException {
        return UriBuilder.fromUri(this.apiHelper.requireLinkUri(this.root.getLinks(), "licenseTypes", this.root.getClass()));
    }
}

