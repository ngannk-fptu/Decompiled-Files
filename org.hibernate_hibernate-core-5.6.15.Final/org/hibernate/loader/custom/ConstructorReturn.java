/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import org.hibernate.loader.custom.Return;
import org.hibernate.loader.custom.ScalarReturn;

public class ConstructorReturn
implements Return {
    private final Class targetClass;
    private final ScalarReturn[] scalars;

    public ConstructorReturn(Class targetClass, ScalarReturn[] scalars) {
        this.targetClass = targetClass;
        this.scalars = scalars;
    }

    public Class getTargetClass() {
        return this.targetClass;
    }

    public ScalarReturn[] getScalars() {
        return this.scalars;
    }
}

