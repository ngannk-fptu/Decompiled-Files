/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client;

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
import com.atlassian.marketplace.client.http.HttpTransport;
import com.atlassian.marketplace.client.model.Links;
import java.io.Closeable;

public interface MarketplaceClient
extends Closeable {
    public boolean isReachable();

    public Addons addons() throws MpacException;

    public AddonCategories addonCategories() throws MpacException;

    public Applications applications() throws MpacException;

    public Assets assets() throws MpacException;

    public LicenseTypes licenseTypes() throws MpacException;

    public Products products() throws MpacException;

    public Vendors vendors() throws MpacException;

    public <T> Page<T> getMore(PageReference<T> var1) throws MpacException;

    public Links getRootLinks() throws MpacException;

    public HttpTransport getHttp();

    public <T> String toJson(T var1) throws MpacException;

    @Override
    public void close();
}

