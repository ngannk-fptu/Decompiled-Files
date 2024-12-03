/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.net.URI;
import java.util.Objects;

public class BaseLinkBuilder {
    private final PermissionEnforcer permissionEnforcer;

    public BaseLinkBuilder(PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    public LinksMapBuilder buildLinkForSelf(URI selfLink) {
        return this.builder().put("self", selfLink);
    }

    public LinksMapBuilder builder() {
        return new LinksMapBuilder(this.permissionEnforcer);
    }
}

