/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState;

public class NameClassNotState
extends NameClassWithChildState {
    protected NameClass castNameClass(NameClass halfCastedNameClass, NameClass child) {
        if (halfCastedNameClass != null) {
            this.reader.reportError("TREXGrammarReader.MoreThanOneNameClass");
            return halfCastedNameClass;
        }
        return new NotNameClass(child);
    }
}

