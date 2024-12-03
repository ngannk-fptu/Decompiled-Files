/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
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
import java.util.Collection;

public class ElementsOfConcernCollector
implements ExpressionVisitor {
    private Collection result;

    public final void collect(Expression exp, Collection result) {
        this.result = result;
        exp.visit(this);
    }

    public final Object onAttribute(AttributeExp exp) {
        return null;
    }

    public final Object onChoice(ChoiceExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }

    public final Object onElement(ElementExp exp) {
        this.result.add(exp);
        return null;
    }

    public final Object onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
        return null;
    }

    public final Object onMixed(MixedExp exp) {
        exp.exp.visit(this);
        return null;
    }

    public final Object onEpsilon() {
        return null;
    }

    public final Object onNullSet() {
        return null;
    }

    public final Object onAnyString() {
        return null;
    }

    public final Object onData(DataExp exp) {
        return null;
    }

    public final Object onValue(ValueExp exp) {
        return null;
    }

    public final Object onList(ListExp exp) {
        return null;
    }

    public final Object onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    public final Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public final Object onSequence(SequenceExp exp) {
        exp.exp1.visit(this);
        if (exp.exp1.isEpsilonReducible()) {
            exp.exp2.visit(this);
        }
        return null;
    }

    public final Object onConcur(ConcurExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }

    public final Object onInterleave(InterleaveExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }
}

