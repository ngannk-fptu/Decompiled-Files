/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.DefineState;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;

public class StartState
extends DefineState {
    protected ReferenceExp getReference() {
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        if (this.startTag.containsAttribute("name")) {
            reader.reportError("GrammarReader.DisallowedAttribute", (Object)this.startTag.qName, (Object)"name");
            return null;
        }
        return reader.getGrammar();
    }
}

