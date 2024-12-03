/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

public interface ExpressionVisitorVoid {
    public void onAttribute(AttributeExp var1);

    public void onChoice(ChoiceExp var1);

    public void onElement(ElementExp var1);

    public void onOneOrMore(OneOrMoreExp var1);

    public void onMixed(MixedExp var1);

    public void onList(ListExp var1);

    public void onRef(ReferenceExp var1);

    public void onOther(OtherExp var1);

    public void onEpsilon();

    public void onNullSet();

    public void onAnyString();

    public void onSequence(SequenceExp var1);

    public void onData(DataExp var1);

    public void onValue(ValueExp var1);

    public void onConcur(ConcurExp var1);

    public void onInterleave(InterleaveExp var1);
}

