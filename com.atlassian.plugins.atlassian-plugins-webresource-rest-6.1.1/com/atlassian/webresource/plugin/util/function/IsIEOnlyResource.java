/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 */
package com.atlassian.webresource.plugin.util.function;

import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.Objects;
import java.util.function.Predicate;

public class IsIEOnlyResource
implements Predicate<PluginUrlResource> {
    private static final IsIEOnlyResource INSTANCE = new IsIEOnlyResource();

    public static IsIEOnlyResource getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean test(PluginUrlResource resource) {
        return Objects.nonNull(resource.getParams().conditionalComment()) || resource.getParams().ieOnly();
    }
}

