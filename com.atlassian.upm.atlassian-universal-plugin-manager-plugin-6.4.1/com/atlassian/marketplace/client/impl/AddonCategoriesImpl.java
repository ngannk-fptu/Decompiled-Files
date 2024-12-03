/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonCategories;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.Applications;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.Application;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Iterator;

final class AddonCategoriesImpl
implements AddonCategories {
    private final ApiHelper apiHelper;
    private final Applications applicationsApi;

    AddonCategoriesImpl(ApiHelper apiHelper, Applications applicationsApi) {
        this.apiHelper = apiHelper;
        this.applicationsApi = applicationsApi;
    }

    @Override
    public Iterable<AddonCategorySummary> findForApplication(ApplicationKey appKey) throws MpacException {
        Iterator<Application> iterator = Convert.iterableOf(this.applicationsApi.safeGetByKey(appKey)).iterator();
        if (iterator.hasNext()) {
            Application a = iterator.next();
            URI uri = this.apiHelper.requireLinkUri(a.getLinks(), "addonCategories", Application.class);
            InternalModel.AddonCategories listRep = this.apiHelper.getEntity(uri, InternalModel.AddonCategories.class);
            return ImmutableList.copyOf(listRep._embedded.categories);
        }
        return ImmutableList.of();
    }
}

