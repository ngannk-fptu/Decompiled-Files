/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import java.util.HashMap;
import java.util.Map;

public class RELAXGrammar
implements Grammar {
    public final Map moduleMap = new HashMap();
    public Expression topLevel;
    public final ExpressionPool pool;

    public Expression getTopLevel() {
        return this.topLevel;
    }

    public ExpressionPool getPool() {
        return this.pool;
    }

    public RELAXGrammar(ExpressionPool pool) {
        this.pool = pool;
    }
}

