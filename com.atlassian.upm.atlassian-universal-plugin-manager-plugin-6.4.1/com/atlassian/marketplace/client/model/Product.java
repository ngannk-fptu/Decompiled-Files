/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ProductVersion;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Iterator;

public final class Product {
    Links _links;
    Embedded _embedded;
    String key;
    String name;
    String summary;

    public Links getLinks() {
        return this._links;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getSummary() {
        return this.summary;
    }

    public Option<URI> getDownloadsPageUri() {
        return this._links.getUri("downloads");
    }

    public Option<ImageInfo> getLogo() {
        return this._embedded.logo;
    }

    public Option<ImageInfo> getTitleLogo() {
        return this._embedded.titleLogo;
    }

    public Option<ProductVersion> getVersion() {
        return this._embedded.version;
    }

    public Option<String> getVersionName() {
        Iterator iterator = this.getVersion().iterator();
        if (iterator.hasNext()) {
            ProductVersion v = (ProductVersion)iterator.next();
            return Option.some((Object)v.getName());
        }
        return Option.none();
    }

    static final class Embedded {
        Option<ImageInfo> logo;
        Option<ImageInfo> titleLogo;
        Option<ProductVersion> version;

        Embedded() {
        }
    }
}

