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

public interface ExpressionVisitorBoolean {
    public boolean onAttribute(AttributeExp var1);

    public boolean onChoice(ChoiceExp var1);

    public boolean onElement(ElementExp var1);

    public boolean onOneOrMore(OneOrMoreExp var1);

    public boolean onMixed(MixedExp var1);

    public boolean onList(ListExp var1);

    public boolean onRef(ReferenceExp var1);

    public boolean onOther(OtherExp var1);

    public boolean onEpsilon();

    public boolean onNullSet();

    public boolean onAnyString();

    public boolean onSequence(SequenceExp var1);

    public boolean onData(DataExp var1);

    public boolean onValue(ValueExp var1);

    public boolean onConcur(ConcurExp var1);

    public boolean onInterleave(InterleaveExp var1);
}

