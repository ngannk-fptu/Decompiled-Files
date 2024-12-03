/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.Collections;
import java.util.List;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

class EnvironmentManagerMetadata
implements ComponentMetadata {
    private final String id;

    public EnvironmentManagerMetadata(String id) {
        this.id = id;
    }

    @Override
    public int getActivation() {
        return 1;
    }

    @Override
    public List<String> getDependsOn() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return this.id;
    }
}

