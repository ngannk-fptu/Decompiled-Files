/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;

public interface RELAXExpressionVisitorBoolean
extends ExpressionVisitorBoolean {
    public boolean onAttPool(AttPoolClause var1);

    public boolean onTag(TagClause var1);

    public boolean onElementRules(ElementRules var1);

    public boolean onHedgeRules(HedgeRules var1);
}

