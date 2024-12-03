/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;

public class TagClause
extends ReferenceExp {
    public NameClass nameClass;
    private static final long serialVersionUID = 1L;

    protected TagClause(String role) {
        super(role);
    }

    public TagClause() {
        super(null);
    }

    public Object visit(RELAXExpressionVisitor visitor) {
        return visitor.onTag(this);
    }

    public Expression visit(RELAXExpressionVisitorExpression visitor) {
        return visitor.onTag(this);
    }

    public boolean visit(RELAXExpressionVisitorBoolean visitor) {
        return visitor.onTag(this);
    }

    public void visit(RELAXExpressionVisitorVoid visitor) {
        visitor.onTag(this);
    }
}

