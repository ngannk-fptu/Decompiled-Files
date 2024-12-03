/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Pattern
 *  javax.validation.constraints.Pattern$Flag
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.cfg.ConstraintDef;

public class PatternDef
extends ConstraintDef<PatternDef, Pattern> {
    public PatternDef() {
        super(Pattern.class);
    }

    public PatternDef flags(Pattern.Flag[] flags) {
        this.addParameter("flags", flags);
        return this;
    }

    public PatternDef regexp(String regexp) {
        this.addParameter("regexp", regexp);
        return this;
    }
}

