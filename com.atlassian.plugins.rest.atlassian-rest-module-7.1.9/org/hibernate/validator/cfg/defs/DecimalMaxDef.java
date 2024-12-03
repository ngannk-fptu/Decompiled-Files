/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.DecimalMax
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.DecimalMax;
import org.hibernate.validator.cfg.ConstraintDef;

public class DecimalMaxDef
extends ConstraintDef<DecimalMaxDef, DecimalMax> {
    public DecimalMaxDef() {
        super(DecimalMax.class);
    }

    public DecimalMaxDef value(String max) {
        this.addParameter("value", max);
        return this;
    }

    public DecimalMaxDef inclusive(boolean inclusive) {
        this.addParameter("inclusive", inclusive);
        return this;
    }
}

