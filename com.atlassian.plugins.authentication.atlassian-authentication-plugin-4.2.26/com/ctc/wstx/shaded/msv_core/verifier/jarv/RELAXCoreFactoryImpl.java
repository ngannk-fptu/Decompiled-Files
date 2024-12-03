/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl;
import org.xml.sax.InputSource;

public class RELAXCoreFactoryImpl
extends FactoryImpl {
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        return RELAXCoreReader.parse(is, this.factory, controller, new ExpressionPool());
    }
}

