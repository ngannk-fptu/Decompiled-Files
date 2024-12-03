/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  org.apache.commons.io.input.NullInputStream
 *  org.apache.http.client.methods.HttpPost
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.AddonVersionSpecifier;
import com.atlassian.marketplace.client.api.AddonVersionsQuery;
import com.atlassian.marketplace.client.api.Addons;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.PricingType;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.http.SimpleHttpResponse;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.ApiImplBase;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonPricing;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionSummary;
import com.atlassian.marketplace.client.util.Convert;
import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.io.input.NullInputStream;
import org.apache.http.client.methods.HttpPost;

final class AddonsImpl
extends ApiImplBase
implements Addons {
    private static final String DATA_CENTER_BUILD_NUMBER_HEADER_NAME = "X-Mpac-DataCenter-BuildNumber";

    AddonsImpl(ApiHelper apiHelper, InternalModel.MinimalLinks root) throws MpacException {
        super(apiHelper, root, "addons");
    }

    @Override
    public Optional<Addon> safeGetByKey(String addonKey, AddonQuery query) throws MpacException {
        InternalModel.Addons collectionRep = this.getEmptyBaseCollectionRep();
        UriTemplate byKeyTemplate = ApiHelper.requireLinkUriTemplate(collectionRep.getLinks(), "byKey", InternalModel.Addons.class);
        UriBuilder uri = UriBuilder.fromUri(byKeyTemplate.resolve((Map<String, String>)ImmutableMap.of((Object)"addonKey", (Object)addonKey)));
        ApiHelper.addAddonQueryParams(query, uri);
        return this.apiHelper.safeGetOptionalEntity(uri.build(), Addon.class);
    }

    @Override
    public Page<AddonSummary> find(AddonQuery query) throws MpacException {
        UriBuilder uri = this.fromApiRoot();
        ApiHelper.addAddonQueryParams(query, uri);
        return this.apiHelper.getMore(new PageReference(uri.build(), query.getBounds(), this.pageReader(InternalModel.Addons.class)));
    }

    @Override
    public Addon createAddon(Addon addon) throws MpacException {
        Function<URI, URI> resultUriTransform = uri -> UriBuilder.fromUri(uri).queryParam("withVersion", true).build();
        return this.genericCreate(this.getApiRoot(), addon, resultUriTransform, this.addDataCenterBuildNumberToHeaders(addon.getDataCenterBuildNumber()));
    }

    private Optional<Consumer<HttpPost>> addDataCenterBuildNumberToHeaders(Option<Long> dataCenterBuildNumber) {
        return Optional.of(post -> dataCenterBuildNumber.forEach(buildNumber -> post.addHeader(DATA_CENTER_BUILD_NUMBER_HEADER_NAME, Long.toString(buildNumber))));
    }

    @Override
    public Addon updateAddon(Addon original, Addon updated) throws MpacException {
        return this.genericUpdate(original.getSelfUri(), original, updated);
    }

    @Override
    public Optional<AddonVersion> safeGetVersion(String addonKey, AddonVersionSpecifier version, AddonVersionsQuery query) throws MpacException {
        AddonQuery queryWithToken = ((AddonQuery.Builder)AddonQuery.builder().accessToken((Optional)query.safeGetAccessToken())).build();
        Iterator<Addon> iterator = Convert.iterableOf(this.safeGetByKey(addonKey, queryWithToken)).iterator();
        if (iterator.hasNext()) {
            Addon a = iterator.next();
            UriBuilder uri = UriBuilder.fromUri(this.getVersionUri(a, version, queryWithToken));
            ApiHelper.addAddonVersionsQueryParams(query, uri);
            return this.apiHelper.safeGetOptionalEntity(uri.build(), AddonVersion.class);
        }
        return Optional.empty();
    }

    @Override
    public Page<AddonVersionSummary> getVersions(String addonKey, AddonVersionsQuery query) throws MpacException {
        Iterator<Addon> iterator = Convert.iterableOf(this.safeGetByKey(addonKey, ((AddonQuery.Builder)AddonQuery.builder().accessToken((Optional)query.safeGetAccessToken())).build())).iterator();
        if (iterator.hasNext()) {
            Addon a = iterator.next();
            UriBuilder uri = UriBuilder.fromUri(this.getVersionsUri(a));
            ApiHelper.addAddonVersionsQueryParams(query, uri);
            return this.apiHelper.getMore(new PageReference(uri.build(), query.getBounds(), this.pageReader(InternalModel.AddonVersions.class)));
        }
        return Page.empty();
    }

    @Override
    public AddonVersion createVersion(String addonKey, AddonVersion version) throws MpacException {
        Iterator<Addon> iterator = Convert.iterableOf(this.safeGetByKey(addonKey, AddonQuery.any())).iterator();
        if (iterator.hasNext()) {
            Addon a = iterator.next();
            return this.genericCreate(this.getVersionsUri(a), version, this.addDataCenterBuildNumberToHeaders(version.getDataCenterBuildNumber()));
        }
        throw new MpacException.ServerError(404);
    }

    @Override
    public AddonVersion updateVersion(AddonVersion original, AddonVersion updated) throws MpacException {
        return this.genericUpdate(original.getSelfUri(), original, updated);
    }

    @Override
    public Optional<AddonPricing> safeGetPricing(String addonKey, PricingType pricingType) throws MpacException {
        for (Addon a : Convert.iterableOf(this.safeGetByKey(addonKey, AddonQuery.any()))) {
            Iterator iterator = a.getPricingUri(pricingType).iterator();
            if (!iterator.hasNext()) continue;
            URI uri = (URI)iterator.next();
            return this.apiHelper.safeGetOptionalEntity(uri, AddonPricing.class);
        }
        return Optional.empty();
    }

    @Override
    public Page<AddonReference> findBanners(AddonQuery query) throws MpacException {
        InternalModel.Addons collectionRep = this.getEmptyBaseCollectionRep();
        UriBuilder uri = UriBuilder.fromUri(this.apiHelper.requireLinkUri(collectionRep.getLinks(), "banners", collectionRep.getClass()));
        ApiHelper.addAddonQueryParams(query, uri);
        return this.apiHelper.getMore(new PageReference(uri.build(), query.getBounds(), this.pageReader(InternalModel.AddonReferences.class)));
    }

    @Override
    public Page<AddonReference> findRecommendations(String addonKey, AddonQuery query) throws MpacException {
        for (Addon a : Convert.iterableOf(this.safeGetByKey(addonKey, AddonQuery.any()))) {
            Iterator iterator = a.getLinks().getUri("recommendations").iterator();
            if (!iterator.hasNext()) continue;
            URI u = (URI)iterator.next();
            UriBuilder uri = UriBuilder.fromUri(u);
            ApiHelper.addAddonQueryParams(query, uri);
            return this.apiHelper.getMore(new PageReference(uri.build(), query.getBounds(), this.pageReader(InternalModel.AddonReferences.class)));
        }
        return Page.empty();
    }

    @Override
    public boolean claimAccessToken(String addonKey, String token) throws MpacException {
        Optional<Addon> addon;
        try {
            addon = this.safeGetByKey(addonKey, ((AddonQuery.Builder)AddonQuery.builder().accessToken((Optional)Optional.of(token))).build());
        }
        catch (MpacException.ServerError e) {
            int status = e.getStatus();
            if (status >= 400 && status < 500) {
                return false;
            }
            throw e;
        }
        for (Addon a : Convert.iterableOf(addon)) {
            Iterator iterator = a.getLinks().getUri("tokens").iterator();
            if (!iterator.hasNext()) continue;
            URI collUri = (URI)iterator.next();
            InternalModel.MinimalLinks rep = this.apiHelper.getEntity(ApiHelper.withZeroLimit(ApiHelper.withAccessToken(collUri, token)), InternalModel.MinimalLinks.class);
            UriTemplate t = ApiHelper.requireLinkUriTemplate(rep.getLinks(), "byToken", rep.getClass());
            URI uri = this.apiHelper.resolveLink(t.resolve((Map<String, String>)ImmutableMap.of((Object)"token", (Object)token)));
            try (SimpleHttpResponse r = this.apiHelper.getHttp().post(uri, (InputStream)new NullInputStream(0L), 0L, "application/json", "application/json", Optional.empty());){
                switch (r.getStatus()) {
                    case 200: 
                    case 204: {
                        boolean bl = true;
                        return bl;
                    }
                    case 400: 
                    case 403: 
                    case 404: {
                        boolean bl = false;
                        return bl;
                    }
                }
                throw this.apiHelper.responseException(r);
            }
        }
        return false;
    }

    private InternalModel.Addons getEmptyBaseCollectionRep() throws MpacException {
        return this.apiHelper.getEntity(ApiHelper.withZeroLimit(this.getApiRoot()), InternalModel.Addons.class);
    }

    private InternalModel.AddonVersions getEmptyVersionCollectionRep(Addon a, AddonQuery query) throws MpacException {
        UriBuilder uri = UriBuilder.fromUri(this.getVersionsUri(a));
        ApiHelper.addAddonQueryParams(query, uri);
        return this.apiHelper.getEntity(ApiHelper.withZeroLimit(uri.build()), InternalModel.AddonVersions.class);
    }

    private URI getVersionsUri(Addon a) throws MpacException {
        return this.apiHelper.requireLinkUri(a.getLinks(), "versions", Addon.class);
    }

    private URI getVersionUri(Addon a, AddonVersionSpecifier v, AddonQuery query) throws MpacException {
        InternalModel.AddonVersions collectionRep = this.getEmptyVersionCollectionRep(a, query);
        for (Either<String, Long> specifiedVersion : Convert.iterableOf(v.getSpecifiedVersion())) {
            Iterator iterator = specifiedVersion.left().iterator();
            if (iterator.hasNext()) {
                String name = (String)iterator.next();
                return ApiHelper.getTemplatedLink(collectionRep, "byName", "name", name);
            }
            iterator = specifiedVersion.right().iterator();
            if (!iterator.hasNext()) continue;
            Long build = (Long)iterator.next();
            return ApiHelper.getTemplatedLink(collectionRep, "byBuild", "pluginBuildNumber", String.valueOf(build));
        }
        return this.apiHelper.requireLinkUri(collectionRep.getLinks(), "latest", InternalModel.AddonVersions.class);
    }
}

