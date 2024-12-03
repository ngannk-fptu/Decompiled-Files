/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.Return;
import org.hibernate.type.Type;

public interface ScalarReturn
extends Return {
    public String getName();

    public Type getType();
}

