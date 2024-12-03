/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.Target;

public interface ReferenceMetadata
extends Target,
ServiceReferenceMetadata {
    public long getTimeout();
}

