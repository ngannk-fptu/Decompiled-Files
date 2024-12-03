/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;

public class HedgeRules
extends ReferenceExp
implements Exportable {
    public boolean exported = false;
    public final RELAXModule ownerModule;
    private static final long serialVersionUID = 1L;

    protected HedgeRules(String label, RELAXModule ownerModule) {
        super(label);
        this.ownerModule = ownerModule;
    }

    public void addHedge(Expression exp, ExpressionPool pool) {
        this.exp = this.exp == null ? exp : pool.createChoice(this.exp, exp);
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onHedgeRules(this);
    }

    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onHedgeRules(this);
    }

    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onHedgeRules(this);
    }

    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onHedgeRules(this);
    }

    public boolean isExported() {
        return this.exported;
    }
}

