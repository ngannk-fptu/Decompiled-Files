/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.models.WebResourceContextKey
 *  com.atlassian.plugin.webresource.models.WebResourceKey
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 */
package com.atlassian.webresource.plugin.async;

import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.plugin.async.model.ResourcesAndData;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface AsyncWebResourceLoader {
    public ResourcesAndData resolve(Map<ResourcePhase, Set<WebResourceKey>> var1, Map<ResourcePhase, Set<WebResourceContextKey>> var2, Set<WebResourceKey> var3, Set<WebResourceContextKey> var4) throws IOException;
}

