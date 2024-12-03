/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.annotators;

import com.atlassian.plugin.webresource.impl.annotators.ResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ListOfAnnotators
extends ResourceContentAnnotator {
    private final List<ResourceContentAnnotator> annotators;

    public ListOfAnnotators(List<ResourceContentAnnotator> annotators) {
        this.annotators = annotators;
    }

    @Override
    public int beforeResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        int offset = 0;
        for (ResourceContentAnnotator annotator : this.annotators) {
            offset += annotator.beforeResourceInBatch(requiredResources, resource, params, stream);
        }
        return offset;
    }

    @Override
    public void afterResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        for (int i = this.annotators.size() - 1; i >= 0; --i) {
            this.annotators.get(i).afterResourceInBatch(requiredResources, resource, params, stream);
        }
    }

    @Override
    public int beforeAllResourcesInBatch(LinkedHashSet<String> requiredResources, String url, Map<String, String> params, OutputStream stream) throws IOException {
        int offset = 0;
        for (ResourceContentAnnotator annotator : this.annotators) {
            offset += annotator.beforeAllResourcesInBatch(requiredResources, url, params, stream);
        }
        return offset;
    }

    @Override
    public void afterAllResourcesInBatch(LinkedHashSet<String> requiredResources, String url, Map<String, String> params, OutputStream stream) throws IOException {
        for (int i = this.annotators.size() - 1; i >= 0; --i) {
            this.annotators.get(i).afterAllResourcesInBatch(requiredResources, url, params, stream);
        }
    }

    @Override
    public int beforeResource(LinkedHashSet<String> requiredResources, String url, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        int offset = 0;
        for (ResourceContentAnnotator annotator : this.annotators) {
            offset += annotator.beforeResource(requiredResources, url, resource, params, stream);
        }
        return offset;
    }

    @Override
    public void afterResource(LinkedHashSet<String> requiredResources, String url, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        for (int i = this.annotators.size() - 1; i >= 0; --i) {
            this.annotators.get(i).afterResource(requiredResources, url, resource, params, stream);
        }
    }

    @Override
    public int hashCode() {
        return this.annotators.hashCode();
    }
}

