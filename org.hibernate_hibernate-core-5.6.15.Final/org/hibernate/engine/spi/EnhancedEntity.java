/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.SelfDirtinessTracker;

public interface EnhancedEntity
extends ManagedEntity,
PersistentAttributeInterceptable,
SelfDirtinessTracker {
}

