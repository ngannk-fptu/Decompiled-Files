/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.AddonVersionSpecifier;
import com.atlassian.marketplace.client.api.AddonVersionsQuery;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PricingType;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonPricing;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionSummary;
import java.util.Optional;

public interface Addons {
    public Optional<Addon> safeGetByKey(String var1, AddonQuery var2) throws MpacException;

    public Page<AddonSummary> find(AddonQuery var1) throws MpacException;

    public Addon createAddon(Addon var1) throws MpacException;

    public Addon updateAddon(Addon var1, Addon var2) throws MpacException;

    public Optional<AddonVersion> safeGetVersion(String var1, AddonVersionSpecifier var2, AddonVersionsQuery var3) throws MpacException;

    public Page<AddonVersionSummary> getVersions(String var1, AddonVersionsQuery var2) throws MpacException;

    public AddonVersion createVersion(String var1, AddonVersion var2) throws MpacException;

    public AddonVersion updateVersion(AddonVersion var1, AddonVersion var2) throws MpacException;

    public Optional<AddonPricing> safeGetPricing(String var1, PricingType var2) throws MpacException;

    public Page<AddonReference> findBanners(AddonQuery var1) throws MpacException;

    public Page<AddonReference> findRecommendations(String var1, AddonQuery var2) throws MpacException;

    public boolean claimAccessToken(String var1, String var2) throws MpacException;
}

