/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.annotators;

import com.atlassian.plugin.webresource.impl.annotators.ResourceContentAnnotator;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.snapshot.resource.ContextResource;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Map;

public class LocationContentAnnotator
extends ResourceContentAnnotator {
    @Override
    public int beforeResourceInBatch(LinkedHashSet<String> requiredResources, Resource resource, Map<String, String> params, OutputStream out) throws IOException {
        if (resource instanceof ContextResource) {
            out.write(("/* loading modules for context \"" + Config.virtualContextKeyToWebResourceKey(resource.getParent().getKey()) + "\" */\n").getBytes());
        } else {
            out.write(String.format("/* module-key = '%s', location = '%s' */\n", resource.getKey(), resource.getLocation()).getBytes());
        }
        return 1;
    }

    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode();
    }
}

