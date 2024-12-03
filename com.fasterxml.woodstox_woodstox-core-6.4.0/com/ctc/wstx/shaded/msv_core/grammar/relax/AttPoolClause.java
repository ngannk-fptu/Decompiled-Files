/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.Exportable;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;

public class AttPoolClause
extends ReferenceExp
implements Exportable {
    public boolean exported = false;
    private static final long serialVersionUID = 1L;

    protected AttPoolClause(String role) {
        super(role);
    }

    public boolean isExported() {
        return this.exported;
    }

    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onAttPool(this);
    }

    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onAttPool(this);
    }

    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onAttPool(this);
    }

    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onAttPool(this);
    }
}

