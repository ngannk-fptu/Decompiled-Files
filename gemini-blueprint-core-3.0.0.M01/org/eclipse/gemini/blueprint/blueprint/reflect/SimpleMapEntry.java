/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NonNullMetadata;

class SimpleMapEntry
implements MapEntry {
    private final NonNullMetadata key;
    private final Metadata value;

    public SimpleMapEntry(NonNullMetadata key, Metadata value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public NonNullMetadata getKey() {
        return this.key;
    }

    @Override
    public Metadata getValue() {
        return this.value;
    }
}

