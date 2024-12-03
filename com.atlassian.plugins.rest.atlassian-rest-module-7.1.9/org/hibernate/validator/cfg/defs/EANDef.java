/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.EAN;

public class EANDef
extends ConstraintDef<EANDef, EAN> {
    public EANDef() {
        super(EAN.class);
    }

    public EANDef type(EAN.Type type) {
        this.addParameter("type", (Object)type);
        return this;
    }
}

