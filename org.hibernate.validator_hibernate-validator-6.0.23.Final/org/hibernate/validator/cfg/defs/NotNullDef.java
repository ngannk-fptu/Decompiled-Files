/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.NotNull
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.cfg.ConstraintDef;

public class NotNullDef
extends ConstraintDef<NotNullDef, NotNull> {
    public NotNullDef() {
        super(NotNull.class);
    }
}

