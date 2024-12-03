/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.VendorId;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.RequiredLink;
import com.atlassian.marketplace.client.model.TopVendorStatus;
import com.atlassian.marketplace.client.model.VendorPrograms;
import io.atlassian.fugue.Option;
import java.net.URI;

public abstract class VendorBase
implements Entity {
    Links _links;
    String name;
    @ReadOnly
    Option<String> verifiedStatus;
    @ReadOnly
    Option<Boolean> isAtlassian;
    VendorPrograms programs;
    @RequiredLink(rel="self")
    URI selfUri;
    @RequiredLink(rel="alternate")
    URI alternateUri;

    @Override
    public Links getLinks() {
        return this._links;
    }

    public VendorId getId() {
        return VendorId.fromUri(this.selfUri);
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public URI getAlternateUri() {
        return this.alternateUri;
    }

    public String getName() {
        return this.name;
    }

    public abstract Option<ImageInfo> getLogo();

    public boolean isVerified() {
        return "verified".equalsIgnoreCase((String)this.verifiedStatus.getOrElse((Object)""));
    }

    public boolean isAtlassian() {
        return this.isAtlassian.exists(Boolean.TRUE::equals);
    }

    public boolean isTopVendor() {
        return this.programs.topVendor.exists(tv -> TopVendorStatus.APPROVED == tv.status);
    }

    public VendorPrograms getPrograms() {
        return this.programs;
    }
}

