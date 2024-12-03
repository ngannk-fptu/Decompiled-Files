/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorExpression;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;

class ExportedAttPoolGenerator
extends ExpressionCloner
implements RELAXExpressionVisitorExpression {
    private String targetNamespace;

    ExportedAttPoolGenerator(ExpressionPool pool) {
        super(pool);
    }

    public Expression create(RELAXModule module, Expression exp) {
        this.targetNamespace = module.targetNamespace;
        return exp.visit(this);
    }

    public Expression onAttribute(AttributeExp exp) {
        if (!(exp.nameClass instanceof SimpleNameClass)) {
            return exp;
        }
        SimpleNameClass nc = (SimpleNameClass)exp.nameClass;
        if (!nc.namespaceURI.equals("")) {
            return exp;
        }
        return this.pool.createAttribute(new SimpleNameClass(this.targetNamespace, nc.localName), exp.exp);
    }

    public Expression onElement(ElementExp exp) {
        throw new Error();
    }

    public Expression onTag(TagClause exp) {
        throw new Error();
    }

    public Expression onElementRules(ElementRules exp) {
        throw new Error();
    }

    public Expression onHedgeRules(HedgeRules exp) {
        throw new Error();
    }

    public Expression onRef(ReferenceExp exp) {
        throw new Error();
    }

    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Expression onAttPool(AttPoolClause exp) {
        return exp.exp.visit(this);
    }
}

