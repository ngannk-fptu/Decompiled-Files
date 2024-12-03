/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.plan.spi.ScalarReturn;
import org.hibernate.type.Type;

public class ScalarReturnImpl
implements ScalarReturn {
    private final String name;
    private final Type type;

    public ScalarReturnImpl(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }
}

