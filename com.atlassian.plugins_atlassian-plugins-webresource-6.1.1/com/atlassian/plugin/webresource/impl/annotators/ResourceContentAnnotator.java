/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.annotators;

import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Map;

public abstract class ResourceContentAnnotator {
    public int beforeResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        return 0;
    }

    public void afterResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
    }

    public int beforeAllResourcesInBatch(LinkedHashSet<String> requiredResources, String url, Map<String, String> params, OutputStream stream) throws IOException {
        return 0;
    }

    public void afterAllResourcesInBatch(LinkedHashSet<String> requiredResources, String url, Map<String, String> params, OutputStream stream) throws IOException {
    }

    public int beforeResource(LinkedHashSet<String> requiredResources, String url, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        return 0;
    }

    public void afterResource(LinkedHashSet<String> requiredResources, String url, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
    }

    public abstract int hashCode();
}

