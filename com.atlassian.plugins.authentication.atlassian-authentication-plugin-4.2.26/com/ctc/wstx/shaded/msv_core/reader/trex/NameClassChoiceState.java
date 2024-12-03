/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState;

public class NameClassChoiceState
extends NameClassWithChildState {
    protected NameClass castNameClass(NameClass halfCasted, NameClass newChild) {
        if (halfCasted == null) {
            return newChild;
        }
        return new ChoiceNameClass(halfCasted, newChild);
    }
}

