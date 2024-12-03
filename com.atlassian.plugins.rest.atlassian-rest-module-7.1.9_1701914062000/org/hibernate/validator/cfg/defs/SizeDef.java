/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Size
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Size;
import org.hibernate.validator.cfg.ConstraintDef;

public class SizeDef
extends ConstraintDef<SizeDef, Size> {
    public SizeDef() {
        super(Size.class);
    }

    public SizeDef min(int min) {
        this.addParameter("min", min);
        return this;
    }

    public SizeDef max(int max) {
        this.addParameter("max", max);
        return this;
    }
}

