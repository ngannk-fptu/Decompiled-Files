/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.model.AddonCategorySummary;

public interface AddonCategories {
    public Iterable<AddonCategorySummary> findForApplication(ApplicationKey var1) throws MpacException;
}

