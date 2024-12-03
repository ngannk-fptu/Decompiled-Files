/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import io.atlassian.fugue.Option;
import java.net.URI;

public class SupportOrganization {
    String name;
    Option<String> supportEmail;
    Option<URI> supportUrl;
    Option<String> supportPhone;

    public String getName() {
        return this.name;
    }

    public Option<String> getSupportEmail() {
        return this.supportEmail;
    }

    public Option<URI> getSupportUrl() {
        return this.supportUrl;
    }

    public Option<String> getSupportPhone() {
        return this.supportPhone;
    }
}

