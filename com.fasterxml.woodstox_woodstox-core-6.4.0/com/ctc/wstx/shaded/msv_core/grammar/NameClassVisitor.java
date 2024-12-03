/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;

public interface NameClassVisitor {
    public Object onChoice(ChoiceNameClass var1);

    public Object onAnyName(AnyNameClass var1);

    public Object onSimple(SimpleNameClass var1);

    public Object onNsName(NamespaceNameClass var1);

    public Object onNot(NotNameClass var1);

    public Object onDifference(DifferenceNameClass var1);
}

