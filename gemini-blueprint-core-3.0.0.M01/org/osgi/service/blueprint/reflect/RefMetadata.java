/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.NonNullMetadata;
import org.osgi.service.blueprint.reflect.Target;

public interface RefMetadata
extends Target,
NonNullMetadata {
    public String getComponentId();
}

