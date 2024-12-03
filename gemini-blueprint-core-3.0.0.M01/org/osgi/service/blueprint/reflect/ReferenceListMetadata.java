/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;

public interface ReferenceListMetadata
extends ServiceReferenceMetadata {
    public static final int USE_SERVICE_OBJECT = 1;
    public static final int USE_SERVICE_REFERENCE = 2;

    public int getMemberType();
}

