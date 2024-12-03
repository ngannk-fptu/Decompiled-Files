/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.List;
import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.PropsMetadata;

class SimplePropsMetadata
implements PropsMetadata {
    private final List<MapEntry> entries;

    public SimplePropsMetadata(List<MapEntry> entries) {
        this.entries = entries;
    }

    @Override
    public List<MapEntry> getEntries() {
        return this.entries;
    }
}

