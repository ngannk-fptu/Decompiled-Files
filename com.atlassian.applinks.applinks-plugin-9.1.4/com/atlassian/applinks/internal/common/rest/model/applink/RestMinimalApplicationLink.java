/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.rest.model.applink;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.rest.model.ApplinksRestRepresentation;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestMinimalApplicationLink
extends ApplinksRestRepresentation {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DISPLAY_URL = "displayUrl";
    public static final String RPC_URL = "rpcUrl";
    public static final String TYPE = "type";
    private String id;
    private String name;
    private URI displayUrl;
    private URI rpcUrl;
    private String type;

    RestMinimalApplicationLink() {
    }

    public RestMinimalApplicationLink(String id, String name, URI displayUrl, URI rpcUrl, String type) {
        this.id = id;
        this.name = name;
        this.displayUrl = displayUrl;
        this.rpcUrl = rpcUrl;
        this.type = type;
    }

    public RestMinimalApplicationLink(@Nonnull ApplicationLink link) {
        Objects.requireNonNull(link, "link");
        this.id = link.getId().get();
        this.name = link.getName();
        this.displayUrl = link.getDisplayUrl();
        this.rpcUrl = link.getRpcUrl();
        this.type = ApplicationTypes.resolveApplicationTypeId(link.getType());
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public URI getDisplayUrl() {
        return this.displayUrl;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }

    public String getType() {
        return this.type;
    }
}

