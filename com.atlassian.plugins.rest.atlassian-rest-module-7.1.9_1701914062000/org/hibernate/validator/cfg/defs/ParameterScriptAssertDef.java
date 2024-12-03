/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.defs;

import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.ParameterScriptAssert;

public class ParameterScriptAssertDef
extends ConstraintDef<ParameterScriptAssertDef, ParameterScriptAssert> {
    public ParameterScriptAssertDef() {
        super(ParameterScriptAssert.class);
    }

    public ParameterScriptAssertDef lang(String lang) {
        this.addParameter("lang", lang);
        return this;
    }

    public ParameterScriptAssertDef script(String script) {
        this.addParameter("script", script);
        return this;
    }
}

