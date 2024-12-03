/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.ProductQuery;
import com.atlassian.marketplace.client.api.ProductVersionSpecifier;
import com.atlassian.marketplace.client.api.Products;
import com.atlassian.marketplace.client.api.QueryBounds;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.ApiImplBase;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.model.Product;
import com.atlassian.marketplace.client.model.ProductVersion;
import com.atlassian.marketplace.client.util.Convert;
import com.atlassian.marketplace.client.util.UriBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;

final class ProductsImpl
extends ApiImplBase
implements Products {
    ProductsImpl(ApiHelper apiHelper, InternalModel.MinimalLinks root) throws MpacException {
        super(apiHelper, root, "products");
    }

    @Override
    public Optional<Product> safeGetByKey(String productKey, ProductQuery query) throws MpacException {
        InternalModel.Products collectionRep = this.getEmptyBaseCollectionRep();
        UriTemplate byKeyTemplate = ApiHelper.requireLinkUriTemplate(collectionRep.getLinks(), "byKey", InternalModel.Products.class);
        UriBuilder uri = UriBuilder.fromUri(byKeyTemplate.resolve((Map<String, String>)ImmutableMap.of((Object)"productKey", (Object)productKey)));
        ApiHelper.addProductQueryParams(query, uri);
        return this.apiHelper.safeGetOptionalEntity(uri.build(), Product.class);
    }

    @Override
    public Optional<ProductVersion> safeGetVersion(String productKey, ProductVersionSpecifier versionQuery) throws MpacException {
        InternalModel.Products collectionRep = this.getEmptyBaseCollectionRep();
        UriTemplate template = ApiHelper.requireLinkUriTemplate(collectionRep.getLinks(), this.getVersionLinkTemplateRel(versionQuery), InternalModel.Products.class);
        ImmutableMap.Builder params = ImmutableMap.builder();
        params.put((Object)"productKey", (Object)productKey);
        for (Integer b : Convert.iterableOf(versionQuery.safeGetBuildNumber())) {
            params.put((Object)"buildNumber", (Object)String.valueOf(b));
        }
        for (String n : Convert.iterableOf(versionQuery.safeGetName())) {
            params.put((Object)"versionName", (Object)n);
        }
        UriBuilder uri = UriBuilder.fromUri(template.resolve((Map<String, String>)params.build()));
        return this.apiHelper.safeGetOptionalEntity(uri.build(), ProductVersion.class);
    }

    private String getVersionLinkTemplateRel(ProductVersionSpecifier versionQuery) {
        return versionQuery.safeGetBuildNumber().isPresent() ? "versionByBuild" : (versionQuery.safeGetName().isPresent() ? "versionByName" : "latestVersion");
    }

    @Override
    public Page<Product> find(ProductQuery query) throws MpacException {
        UriBuilder uri = this.fromApiRoot();
        ApiHelper.addProductQueryParams(query, uri);
        return this.apiHelper.getMore(new PageReference(uri.build(), query.getBounds(), this.pageReader(InternalModel.Products.class)));
    }

    private InternalModel.Products getEmptyBaseCollectionRep() throws MpacException {
        UriBuilder uri = this.fromApiRoot();
        ApiHelper.addBoundsParams(ProductQuery.builder().bounds(QueryBounds.empty()).build(), uri);
        return this.apiHelper.getEntity(uri.build(), InternalModel.Products.class);
    }
}

