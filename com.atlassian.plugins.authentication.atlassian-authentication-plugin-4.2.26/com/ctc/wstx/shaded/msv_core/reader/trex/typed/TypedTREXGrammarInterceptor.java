/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.typed;

import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.typed.TypedElementState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class TypedTREXGrammarInterceptor
extends TREXGrammarReader.StateFactory {
    public static final String LABEL_NAMESPACE = "http://www.sun.com/xml/msv/trex-type";

    public State element(State parent, StartTagInfo tag) {
        return new TypedElementState();
    }
}

