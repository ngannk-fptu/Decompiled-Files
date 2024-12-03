/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.trex;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;

public class ElementPattern
extends ElementExp {
    public final NameClass nameClass;
    private static final long serialVersionUID = 1L;

    public final NameClass getNameClass() {
        return this.nameClass;
    }

    public ElementPattern(NameClass nameClass, Expression contentModel) {
        super(contentModel, false);
        this.nameClass = nameClass;
    }
}

