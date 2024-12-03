/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Digits
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Digits;
import org.hibernate.validator.cfg.ConstraintDef;

public class DigitsDef
extends ConstraintDef<DigitsDef, Digits> {
    public DigitsDef() {
        super(Digits.class);
    }

    public DigitsDef integer(int integer) {
        this.addParameter("integer", integer);
        return this;
    }

    public DigitsDef fraction(int fraction) {
        this.addParameter("fraction", fraction);
        return this;
    }
}

