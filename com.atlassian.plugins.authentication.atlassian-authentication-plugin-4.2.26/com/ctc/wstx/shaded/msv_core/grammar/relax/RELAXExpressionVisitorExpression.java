/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;

public interface RELAXExpressionVisitorExpression
extends ExpressionVisitorExpression {
    public Expression onAttPool(AttPoolClause var1);

    public Expression onTag(TagClause var1);

    public Expression onElementRules(ElementRules var1);

    public Expression onHedgeRules(HedgeRules var1);
}

