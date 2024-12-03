/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.wiring;

import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.lang.Nullable;

public interface BeanWiringInfoResolver {
    @Nullable
    public BeanWiringInfo resolveWiringInfo(Object var1);
}

