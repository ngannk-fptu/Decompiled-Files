/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.Range;

public class RangeDef
extends ConstraintDef<RangeDef, Range> {
    public RangeDef() {
        super(Range.class);
    }

    public RangeDef min(long min) {
        this.addParameter("min", min);
        return this;
    }

    public RangeDef max(long max) {
        this.addParameter("max", max);
        return this;
    }
}

