/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.ConstraintDef;

public interface Constrainable<C extends Constrainable<C>> {
    public C constraint(ConstraintDef<?, ?> var1);
}

