/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.NonNullMetadata;

public interface ValueMetadata
extends NonNullMetadata {
    public String getStringValue();

    public String getType();
}

