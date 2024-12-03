/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl;
import org.xml.sax.InputSource;

public class XSFactoryImpl
extends FactoryImpl {
    protected Grammar parse(InputSource is, GrammarReaderController controller) {
        return XMLSchemaReader.parse(is, this.factory, controller);
    }
}

