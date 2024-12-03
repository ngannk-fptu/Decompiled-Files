/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  io.atlassian.fugue.Either
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.ApplicationVersionSpecifier;
import com.atlassian.marketplace.client.api.ApplicationVersionsQuery;
import com.atlassian.marketplace.client.api.Applications;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.ProductQuery;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.ApiImplBase;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.Application;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.util.Convert;
import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableMap;
import io.atlassian.fugue.Either;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

final class ApplicationsImpl
extends ApiImplBase
implements Applications {
    ApplicationsImpl(ApiHelper apiHelper, InternalModel.MinimalLinks root) throws MpacException {
        super(apiHelper, root, "applications");
    }

    @Override
    public Optional<Application> safeGetByKey(ApplicationKey applicationKey) throws MpacException {
        InternalModel.Applications collectionRep = this.getEmptyBaseCollectionRep();
        UriTemplate byKeyTemplate = ApiHelper.requireLinkUriTemplate(collectionRep._links, "byKey", InternalModel.Applications.class);
        UriBuilder uri = UriBuilder.fromUri(byKeyTemplate.resolve((Map<String, String>)ImmutableMap.of((Object)"applicationKey", (Object)applicationKey.getKey())));
        return this.apiHelper.safeGetOptionalEntity(uri.build(), Application.class);
    }

    @Override
    public Optional<ApplicationVersion> safeGetVersion(ApplicationKey applicationKey, ApplicationVersionSpecifier versionQuery) throws MpacException {
        Iterator<Application> iterator = Convert.iterableOf(this.safeGetByKey(applicationKey)).iterator();
        if (iterator.hasNext()) {
            Application a = iterator.next();
            URI uri = this.getVersionUri(a, versionQuery);
            return this.apiHelper.safeGetOptionalEntity(uri, ApplicationVersion.class);
        }
        return Optional.empty();
    }

    @Override
    public Page<ApplicationVersion> getVersions(ApplicationKey applicationKey, ApplicationVersionsQuery versionsQuery) throws MpacException {
        Iterator<Application> iterator = Convert.iterableOf(this.safeGetByKey(applicationKey)).iterator();
        if (iterator.hasNext()) {
            Application a = iterator.next();
            UriTemplate ut = this.getVersionsUriTemplate(a);
            ImmutableMap.Builder params = ImmutableMap.builder();
            for (int b : Convert.iterableOf(versionsQuery.safeGetAfterBuildNumber())) {
                params.put((Object)"afterBuildNumber", (Object)String.valueOf(b));
            }
            UriBuilder ub = UriBuilder.fromUri(ut.resolve((Map<String, String>)params.build()));
            ApiHelper.addHostingParam(versionsQuery, ub);
            ApiHelper.addBoundsParams(versionsQuery, ub);
            return this.apiHelper.getMore(new PageReference(ub.build(), versionsQuery.getBounds(), this.pageReader(InternalModel.ApplicationVersions.class)));
        }
        return Page.empty();
    }

    @Override
    public ApplicationVersion createVersion(ApplicationKey applicationKey, ApplicationVersion version) throws MpacException {
        Iterator<Application> iterator = Convert.iterableOf(this.safeGetByKey(applicationKey)).iterator();
        if (iterator.hasNext()) {
            Application a = iterator.next();
            return this.genericCreate(this.getVersionsUriTemplate(a).resolve((Map<String, String>)ImmutableMap.of()), version, Optional.empty());
        }
        throw new MpacException.ServerError(404);
    }

    @Override
    public ApplicationVersion updateVersion(ApplicationVersion original, ApplicationVersion updated) throws MpacException {
        return this.genericUpdate(original.getSelfUri(), original, updated);
    }

    private UriTemplate getVersionsUriTemplate(Application a) throws MpacException {
        return ApiHelper.requireLinkUriTemplate(a.getLinks(), "versions", Application.class);
    }

    private InternalModel.Applications getEmptyBaseCollectionRep() throws MpacException {
        UriBuilder uri = this.fromApiRoot();
        ApiHelper.addBoundsParams(ProductQuery.builder().bounds(QueryBounds.empty()).build(), uri);
        return this.apiHelper.getEntity(uri.build(), InternalModel.Applications.class);
    }

    private URI getVersionUri(Application a, ApplicationVersionSpecifier v) throws MpacException {
        for (Either<String, Integer> specifiedVersion : Convert.iterableOf(v.safeGetSpecifiedVersion())) {
            Iterator iterator = specifiedVersion.left().iterator();
            if (iterator.hasNext()) {
                String name = (String)iterator.next();
                return ApiHelper.getTemplatedLink(a, "versionByName", "name", name);
            }
            iterator = specifiedVersion.right().iterator();
            if (!iterator.hasNext()) continue;
            Integer build = (Integer)iterator.next();
            return ApiHelper.getTemplatedLink(a, "versionByBuild", "applicationBuildNumber", String.valueOf(build));
        }
        return this.apiHelper.requireLinkUri(a.getLinks(), "latestVersion", Application.class);
    }
}

