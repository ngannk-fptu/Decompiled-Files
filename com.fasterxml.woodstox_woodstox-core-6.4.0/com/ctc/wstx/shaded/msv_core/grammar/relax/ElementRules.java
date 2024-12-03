/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;

public class ElementRules
extends ReferenceExp
implements Exportable {
    public boolean exported = false;
    public final RELAXModule ownerModule;
    private static final long serialVersionUID = 1L;

    protected ElementRules(String label, RELAXModule ownerModule) {
        super(label);
        this.ownerModule = ownerModule;
    }

    public boolean equals(Object o) {
        return this == o;
    }

    protected boolean calcEpsilonReducibility() {
        return false;
    }

    public void addElementRule(ExpressionPool pool, ElementRule newRule) {
        newRule.parent = this;
        this.exp = this.exp == null ? newRule : pool.createChoice(this.exp, newRule);
    }

    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onElementRules(this);
    }

    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onElementRules(this);
    }

    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onElementRules(this);
    }

    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onElementRules(this);
    }

    public boolean isExported() {
        return this.exported;
    }
}

