/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.osgi.service.blueprint.reflect.IdRefMetadata;

class SimpleIdRefMetadata
implements IdRefMetadata {
    private final String referenceName;

    public SimpleIdRefMetadata(String name) {
        this.referenceName = name;
    }

    @Override
    public String getComponentId() {
        return this.referenceName;
    }
}

