/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

public class ContentModelRefExpRemover {

    private static class Remover
    extends ExpressionCloner {
        public Remover(ExpressionPool pool) {
            super(pool);
        }

        public Expression onElement(ElementExp exp) {
            return exp;
        }

        public Expression onAttribute(AttributeExp exp) {
            Expression content = exp.exp.visit(this);
            if (content == Expression.nullSet) {
                return Expression.nullSet;
            }
            return this.pool.createAttribute(exp.nameClass, content);
        }

        public Expression onRef(ReferenceExp exp) {
            return exp.exp.visit(this);
        }

        public Expression onOther(OtherExp exp) {
            return exp.exp.visit(this);
        }
    }
}

