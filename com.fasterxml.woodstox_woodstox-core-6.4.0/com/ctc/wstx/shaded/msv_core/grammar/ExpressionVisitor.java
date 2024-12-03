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

public interface ExpressionVisitor {
    public Object onAttribute(AttributeExp var1);

    public Object onChoice(ChoiceExp var1);

    public Object onElement(ElementExp var1);

    public Object onOneOrMore(OneOrMoreExp var1);

    public Object onMixed(MixedExp var1);

    public Object onList(ListExp var1);

    public Object onRef(ReferenceExp var1);

    public Object onOther(OtherExp var1);

    public Object onEpsilon();

    public Object onNullSet();

    public Object onAnyString();

    public Object onSequence(SequenceExp var1);

    public Object onData(DataExp var1);

    public Object onValue(ValueExp var1);

    public Object onConcur(ConcurExp var1);

    public Object onInterleave(InterleaveExp var1);
}

