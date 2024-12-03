/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.spi;

import org.hibernate.LockMode;
import org.hibernate.loader.plan.spi.EntityReference;

public interface LockModeResolver {
    public LockMode resolveLockMode(EntityReference var1);
}

