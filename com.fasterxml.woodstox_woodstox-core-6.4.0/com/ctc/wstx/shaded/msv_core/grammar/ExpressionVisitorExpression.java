/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

public interface ExpressionVisitorExpression {
    public Expression onAttribute(AttributeExp var1);

    public Expression onChoice(ChoiceExp var1);

    public Expression onElement(ElementExp var1);

    public Expression onOneOrMore(OneOrMoreExp var1);

    public Expression onMixed(MixedExp var1);

    public Expression onList(ListExp var1);

    public Expression onRef(ReferenceExp var1);

    public Expression onOther(OtherExp var1);

    public Expression onEpsilon();

    public Expression onNullSet();

    public Expression onAnyString();

    public Expression onSequence(SequenceExp var1);

    public Expression onData(DataExp var1);

    public Expression onValue(ValueExp var1);

    public Expression onConcur(ConcurExp var1);

    public Expression onInterleave(InterleaveExp var1);
}

