/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.osgi.service.blueprint.reflect.RefMetadata;

class SimpleRefMetadata
implements RefMetadata {
    private final String componentId;

    public SimpleRefMetadata(String id) {
        this.componentId = id;
    }

    @Override
    public String getComponentId() {
        return this.componentId;
    }
}

