/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.engine.spi.PersistentAttributeInterceptor;

public interface PersistentAttributeInterceptable {
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor();

    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor var1);
}

