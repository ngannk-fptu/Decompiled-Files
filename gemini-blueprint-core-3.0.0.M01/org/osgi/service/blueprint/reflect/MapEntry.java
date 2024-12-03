/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NonNullMetadata;

public interface MapEntry {
    public NonNullMetadata getKey();

    public Metadata getValue();
}

