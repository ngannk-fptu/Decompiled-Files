/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.ProductQuery;
import com.atlassian.marketplace.client.api.ProductVersionSpecifier;
import com.atlassian.marketplace.client.model.Product;
import com.atlassian.marketplace.client.model.ProductVersion;
import java.util.Optional;

public interface Products {
    public Optional<Product> safeGetByKey(String var1, ProductQuery var2) throws MpacException;

    public Optional<ProductVersion> safeGetVersion(String var1, ProductVersionSpecifier var2) throws MpacException;

    public Page<Product> find(ProductQuery var1) throws MpacException;
}

