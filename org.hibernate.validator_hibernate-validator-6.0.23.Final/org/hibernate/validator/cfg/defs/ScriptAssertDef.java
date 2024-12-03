/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.ScriptAssert;

public class ScriptAssertDef
extends ConstraintDef<ScriptAssertDef, ScriptAssert> {
    public ScriptAssertDef() {
        super(ScriptAssert.class);
    }

    public ScriptAssertDef lang(String lang) {
        this.addParameter("lang", lang);
        return this;
    }

    public ScriptAssertDef script(String script) {
        this.addParameter("script", script);
        return this;
    }

    public ScriptAssertDef alias(String alias) {
        this.addParameter("alias", alias);
        return this;
    }

    public ScriptAssertDef reportOn(String reportOn) {
        this.addParameter("reportOn", reportOn);
        return this;
    }
}

