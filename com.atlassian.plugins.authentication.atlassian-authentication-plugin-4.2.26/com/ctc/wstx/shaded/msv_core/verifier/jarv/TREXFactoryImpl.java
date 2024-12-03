/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl;
import org.xml.sax.InputSource;

public class TREXFactoryImpl
extends FactoryImpl {
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        return TREXGrammarReader.parse(is, this.factory, controller);
    }
}

