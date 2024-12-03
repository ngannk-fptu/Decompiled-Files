/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MarketplaceClient;
import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonCategories;
import com.atlassian.marketplace.client.api.Addons;
import com.atlassian.marketplace.client.api.Applications;
import com.atlassian.marketplace.client.api.Assets;
import com.atlassian.marketplace.client.api.LicenseTypes;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import com.atlassian.marketplace.client.api.Products;
import com.atlassian.marketplace.client.api.Vendors;
import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.http.HttpTransport;
import com.atlassian.marketplace.client.impl.AddonCategoriesImpl;
import com.atlassian.marketplace.client.impl.AddonsImpl;
import com.atlassian.marketplace.client.impl.ApiHelper;
import com.atlassian.marketplace.client.impl.ApplicationsImpl;
import com.atlassian.marketplace.client.impl.AssetsImpl;
import com.atlassian.marketplace.client.impl.CommonsHttpTransport;
import com.atlassian.marketplace.client.impl.EntityEncoding;
import com.atlassian.marketplace.client.impl.InternalModel;
import com.atlassian.marketplace.client.impl.JsonEntityEncoding;
import com.atlassian.marketplace.client.impl.LicenseTypesImpl;
import com.atlassian.marketplace.client.impl.ProductsImpl;
import com.atlassian.marketplace.client.impl.VendorsImpl;
import com.atlassian.marketplace.client.model.Links;
import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

public final class DefaultMarketplaceClient
implements MarketplaceClient {
    public static final URI DEFAULT_SERVER_URI = URI.create("https://marketplace.atlassian.com");
    protected final URI baseUri;
    protected final HttpTransport httpTransport;
    protected final EntityEncoding encoding;
    public static final String API_VERSION = "2";
    private final ApiHelper apiHelper;

    public DefaultMarketplaceClient(URI baseUri, HttpConfiguration configuration) {
        this(baseUri, new CommonsHttpTransport(configuration, baseUri), new JsonEntityEncoding());
    }

    public DefaultMarketplaceClient(URI serverBaseUri, HttpTransport httpTransport, EntityEncoding encoding) {
        this.baseUri = ApiHelper.normalizeBaseUri((URI)Preconditions.checkNotNull((Object)serverBaseUri, (Object)"serverBaseUri")).resolve("rest/2/");
        this.httpTransport = (HttpTransport)Preconditions.checkNotNull((Object)httpTransport, (Object)"httpTransport");
        this.encoding = encoding;
        this.apiHelper = new ApiHelper(this.baseUri, httpTransport, encoding);
    }

    @Override
    public void close() {
        try {
            this.httpTransport.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public boolean isReachable() {
        return this.apiHelper.checkReachable(this.baseUri);
    }

    @Override
    public Links getRootLinks() throws MpacException {
        return this.getRoot().getLinks();
    }

    @Override
    public HttpTransport getHttp() {
        return this.httpTransport;
    }

    @Override
    public <T> String toJson(T entity) throws MpacException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.encoding.encode(os, entity, true);
        return new String(os.toByteArray());
    }

    @Override
    public Addons addons() throws MpacException {
        return new AddonsImpl(this.apiHelper, this.getRoot());
    }

    @Override
    public AddonCategories addonCategories() throws MpacException {
        return new AddonCategoriesImpl(this.apiHelper, this.applications());
    }

    @Override
    public Applications applications() throws MpacException {
        return new ApplicationsImpl(this.apiHelper, this.getRoot());
    }

    @Override
    public Assets assets() throws MpacException {
        return new AssetsImpl(this.apiHelper, this.getRoot());
    }

    @Override
    public LicenseTypes licenseTypes() throws MpacException {
        return new LicenseTypesImpl(this.apiHelper, this.getRoot());
    }

    @Override
    public Products products() throws MpacException {
        return new ProductsImpl(this.apiHelper, this.getRoot());
    }

    @Override
    public Vendors vendors() throws MpacException {
        return new VendorsImpl(this.apiHelper, this.getRoot());
    }

    @Override
    public <T> Page<T> getMore(PageReference<T> ref) throws MpacException {
        return this.apiHelper.getMore(ref);
    }

    InternalModel.MinimalLinks getRoot() throws MpacException {
        return this.apiHelper.getEntity(this.baseUri, InternalModel.MinimalLinks.class);
    }
}

