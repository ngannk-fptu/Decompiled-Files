/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.DecisionEventInfo;

public class ContextSensitivityInfo
extends DecisionEventInfo {
    public ContextSensitivityInfo(int decision, ATNConfigSet configs, TokenStream input, int startIndex, int stopIndex) {
        super(decision, configs, input, startIndex, stopIndex, true);
    }
}

