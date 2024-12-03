/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import java.util.Set;
import javax.annotation.Nonnull;

public interface ResourceDependencyResolver {
    public Iterable<ModuleDescriptorStub> getSuperBatchDependencies();

    public Iterable<ModuleDescriptorStub> getDependencies(RequestCache var1, UrlBuildingStrategy var2, String var3, boolean var4, boolean var5);

    public Iterable<ModuleDescriptorStub> getDependenciesInContext(RequestCache var1, UrlBuildingStrategy var2, @Nonnull String var3, @Nonnull Set<String> var4, boolean var5);
}

