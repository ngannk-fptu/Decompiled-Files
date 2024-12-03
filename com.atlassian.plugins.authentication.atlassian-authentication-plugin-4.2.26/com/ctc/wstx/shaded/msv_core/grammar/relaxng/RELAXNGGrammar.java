/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relaxng;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;

public class RELAXNGGrammar
extends TREXGrammar {
    public boolean isIDcompatible = true;
    public boolean isDefaultAttributeValueCompatible = true;
    public boolean isAnnotationCompatible = true;
    private static final long serialVersionUID = 1L;

    public RELAXNGGrammar(ExpressionPool pool, TREXGrammar parentGrammar) {
        super(pool, parentGrammar);
    }

    public RELAXNGGrammar(ExpressionPool pool) {
        super(pool);
    }

    public RELAXNGGrammar() {
    }
}

