/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.rest.model.applink;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.rest.model.applink.RestMinimalApplicationLink;
import com.atlassian.applinks.internal.rest.model.IllegalRestRepresentationStateException;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nonnull;

public class RestApplicationLink
extends RestMinimalApplicationLink {
    public static final String SYSTEM = "system";
    public static final String PRIMARY = "primary";
    private boolean system;
    private boolean primary;

    public boolean isSystem() {
        return this.system;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public RestApplicationLink() {
    }

    public RestApplicationLink(String name, String rpcUrl, String displayUrl, boolean primary) throws URISyntaxException {
        super("", name, RestApplicationLink.asURI(displayUrl), RestApplicationLink.asURI(rpcUrl), "");
        this.system = false;
        this.primary = primary;
    }

    public RestApplicationLink(@Nonnull ApplicationLink link) {
        super(link);
        this.system = link.isSystem();
        this.primary = link.isPrimary();
    }

    @Nonnull
    public ApplicationLinkDetails toDetails() throws IllegalRestRepresentationStateException {
        return ApplicationLinkDetails.builder().name(this.validateString("name", this.getName())).rpcUrl(this.validateURI("rpcUrl", this.getRpcUrl())).displayUrl(this.validateURI("displayUrl", this.getDisplayUrl())).isPrimary(this.isPrimary()).build();
    }

    private static URI asURI(String uri) throws URISyntaxException {
        return uri == null ? null : new URI(uri);
    }
}

