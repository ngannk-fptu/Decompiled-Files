/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;

public class SkipElementExp
extends ElementPattern {
    private static final long serialVersionUID = 1L;

    public SkipElementExp(NameClass nameClass, Expression contentModel) {
        super(nameClass, contentModel);
    }
}

