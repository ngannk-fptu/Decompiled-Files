/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.trex.typed;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;

public class TypedElementPattern
extends ElementPattern {
    public final String label;
    private static final long serialVersionUID = 1L;

    public TypedElementPattern(NameClass nameClass, Expression contentModel, String label) {
        super(nameClass, contentModel);
        this.label = label;
    }
}

