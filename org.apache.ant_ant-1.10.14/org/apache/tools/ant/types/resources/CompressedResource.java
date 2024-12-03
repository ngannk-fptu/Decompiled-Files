/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.ContentTransformingResource;

public abstract class CompressedResource
extends ContentTransformingResource {
    protected CompressedResource() {
    }

    protected CompressedResource(ResourceCollection other) {
        this.addConfigured(other);
    }

    @Override
    public String toString() {
        return this.getCompressionName() + " compressed " + super.toString();
    }

    protected abstract String getCompressionName();
}

