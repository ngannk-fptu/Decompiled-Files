/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.VendorExternalLinkType;
import com.atlassian.marketplace.client.model.Address;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.SupportDetails;
import com.atlassian.marketplace.client.model.VendorBase;
import io.atlassian.fugue.Option;
import java.net.URI;
import java.util.Map;

public final class Vendor
extends VendorBase {
    @ReadOnly
    Embedded _embedded;
    Option<String> description;
    Option<Address> address;
    String email;
    Option<String> phone;
    Map<String, URI> vendorLinks;
    Option<String> otherContactDetails;
    SupportDetails supportDetails = new SupportDetails();

    public Option<String> getDescription() {
        return this.description;
    }

    public String getEmail() {
        return this.email;
    }

    public Option<Address> getAddress() {
        return this.address;
    }

    public Option<String> getPhone() {
        return this.phone;
    }

    public Option<String> getOtherContactDetails() {
        return this.otherContactDetails;
    }

    public SupportDetails getSupportDetails() {
        return this.supportDetails;
    }

    public Option<URI> getExternalLinkUri(VendorExternalLinkType type) {
        return Option.option((Object)this.vendorLinks.get(type.getKey()));
    }

    @Override
    public Option<ImageInfo> getLogo() {
        return this._embedded.logo;
    }

    static final class Embedded {
        Option<ImageInfo> logo;

        Embedded() {
        }
    }
}

