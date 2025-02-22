/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.ISBN;

public class ISBNDef
extends ConstraintDef<ISBNDef, ISBN> {
    public ISBNDef() {
        super(ISBN.class);
    }

    public ISBNDef type(ISBN.Type type) {
        this.addParameter("type", (Object)type);
        return this;
    }
}

