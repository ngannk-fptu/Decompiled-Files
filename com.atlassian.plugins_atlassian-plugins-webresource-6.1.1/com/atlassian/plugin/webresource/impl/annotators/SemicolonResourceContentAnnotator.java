/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.annotators;

import com.atlassian.plugin.webresource.impl.annotators.ResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Map;

public class SemicolonResourceContentAnnotator
extends ResourceContentAnnotator {
    @Override
    public int beforeResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        stream.write(59);
        stream.write(10);
        return 1;
    }

    @Override
    public void afterResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream stream) throws IOException {
        stream.write(59);
    }

    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode();
    }
}

