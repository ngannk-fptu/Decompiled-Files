/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.constraints.Email
 *  javax.validation.constraints.Pattern$Flag
 */
package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.cfg.ConstraintDef;

public class EmailDef
extends ConstraintDef<EmailDef, Email> {
    public EmailDef() {
        super(Email.class);
    }

    public EmailDef regexp(String regexp) {
        this.addParameter("regexp", regexp);
        return this;
    }

    public EmailDef flags(Pattern.Flag ... flags) {
        this.addParameter("flags", flags);
        return this;
    }
}

