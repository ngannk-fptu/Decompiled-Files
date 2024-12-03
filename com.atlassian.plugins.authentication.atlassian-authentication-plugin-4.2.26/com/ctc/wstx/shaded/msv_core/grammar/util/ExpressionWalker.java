/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

public abstract class ExpressionWalker
implements ExpressionVisitorVoid {
    public void onRef(ReferenceExp exp) {
        exp.exp.visit(this);
    }

    public void onOther(OtherExp exp) {
        exp.exp.visit(this);
    }

    public void onElement(ElementExp exp) {
        exp.contentModel.visit(this);
    }

    public void onEpsilon() {
    }

    public void onNullSet() {
    }

    public void onAnyString() {
    }

    public void onData(DataExp exp) {
    }

    public void onValue(ValueExp exp) {
    }

    public void onInterleave(InterleaveExp exp) {
        this.onBinExp(exp);
    }

    public void onConcur(ConcurExp exp) {
        this.onBinExp(exp);
    }

    public void onChoice(ChoiceExp exp) {
        this.onBinExp(exp);
    }

    public void onSequence(SequenceExp exp) {
        this.onBinExp(exp);
    }

    public void onBinExp(BinaryExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
    }

    public void onMixed(MixedExp exp) {
        exp.exp.visit(this);
    }

    public void onList(ListExp exp) {
        exp.exp.visit(this);
    }

    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
    }

    public void onAttribute(AttributeExp exp) {
        exp.exp.visit(this);
    }
}

