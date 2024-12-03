/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.PluginDetails
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.diagnostics.internal.rest.RestEntity;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestPluginDetails
extends RestEntity {
    public RestPluginDetails(@Nonnull PluginDetails pluginDetails) {
        Objects.requireNonNull(pluginDetails, "pluginDetails");
        this.put("key", Objects.requireNonNull(pluginDetails.getKey(), "key"));
        this.put("name", Objects.requireNonNull(pluginDetails.getName(), "name"));
        this.putIfNotNull("version", pluginDetails.getVersion());
    }
}

