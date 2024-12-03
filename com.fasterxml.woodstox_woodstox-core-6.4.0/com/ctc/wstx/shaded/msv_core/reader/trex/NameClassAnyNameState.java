/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithoutChildState;

public class NameClassAnyNameState
extends NameClassWithoutChildState {
    protected NameClass makeNameClass() {
        return NameClass.ALL;
    }
}

