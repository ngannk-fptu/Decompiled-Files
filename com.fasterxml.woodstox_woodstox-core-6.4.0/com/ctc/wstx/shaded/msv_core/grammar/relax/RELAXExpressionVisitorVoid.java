/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;

public interface RELAXExpressionVisitorVoid
extends ExpressionVisitorVoid {
    public void onAttPool(AttPoolClause var1);

    public void onTag(TagClause var1);

    public void onElementRules(ElementRules var1);

    public void onHedgeRules(HedgeRules var1);
}

