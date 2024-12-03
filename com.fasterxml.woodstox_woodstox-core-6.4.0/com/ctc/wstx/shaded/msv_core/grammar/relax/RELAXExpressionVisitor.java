/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;

public interface RELAXExpressionVisitor
extends ExpressionVisitor {
    public Object onAttPool(AttPoolClause var1);

    public Object onTag(TagClause var1);

    public Object onElementRules(ElementRules var1);

    public Object onHedgeRules(HedgeRules var1);
}

