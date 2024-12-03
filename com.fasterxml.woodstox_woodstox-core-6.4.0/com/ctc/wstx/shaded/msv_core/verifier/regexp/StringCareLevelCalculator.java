/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

public class StringCareLevelCalculator
implements ExpressionVisitor {
    protected static final StringCareLevelCalculator theInstance = new StringCareLevelCalculator();
    private static final String ANY_STRING = "anyString";
    private static final String NO_STRING = "noString";
    private static final String SOME_STRING = "someString";

    protected StringCareLevelCalculator() {
    }

    public Object onAttribute(AttributeExp exp) {
        return NO_STRING;
    }

    public Object onElement(ElementExp exp) {
        return NO_STRING;
    }

    public Object onMixed(MixedExp exp) {
        return ANY_STRING;
    }

    public Object onList(ListExp exp) {
        return SOME_STRING;
    }

    public Object onAnyString() {
        return ANY_STRING;
    }

    public Object onData(DataExp exp) {
        XSDatatype xdt;
        if (exp.except == Expression.nullSet && exp.dt instanceof XSDatatype && (xdt = (XSDatatype)exp.dt).isAlwaysValid()) {
            return ANY_STRING;
        }
        return SOME_STRING;
    }

    public Object onValue(ValueExp exp) {
        return SOME_STRING;
    }

    public Object onChoice(ChoiceExp exp) {
        return this.doChoice(exp);
    }

    private Object doChoice(BinaryExp exp) {
        Object lhs = exp.exp1.visit(this);
        Object rhs = exp.exp2.visit(this);
        if (lhs == ANY_STRING && rhs == ANY_STRING) {
            return ANY_STRING;
        }
        if (lhs == NO_STRING && rhs == NO_STRING) {
            return NO_STRING;
        }
        return SOME_STRING;
    }

    public Object onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit(this);
    }

    public Object onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Object onEpsilon() {
        return NO_STRING;
    }

    public Object onNullSet() {
        return NO_STRING;
    }

    public Object onSequence(SequenceExp exp) {
        if (!exp.exp1.isEpsilonReducible()) {
            return exp.exp1.visit(this);
        }
        return this.doChoice(exp);
    }

    public Object onConcur(ConcurExp exp) {
        Object lhs = exp.exp1.visit(this);
        Object rhs = exp.exp2.visit(this);
        if (lhs == ANY_STRING && rhs == ANY_STRING) {
            return ANY_STRING;
        }
        if (lhs == NO_STRING || rhs == NO_STRING) {
            return NO_STRING;
        }
        return SOME_STRING;
    }

    public Object onInterleave(InterleaveExp p) {
        return this.doChoice(p);
    }

    public static int calc(Expression exp) {
        Object r = exp.visit(theInstance);
        if (r == ANY_STRING) {
            return 1;
        }
        if (r == NO_STRING) {
            return 0;
        }
        return 2;
    }
}

