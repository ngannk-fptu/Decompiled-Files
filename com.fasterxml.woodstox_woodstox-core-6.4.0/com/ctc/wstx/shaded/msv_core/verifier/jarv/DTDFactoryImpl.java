/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.jarv;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.dtd.DTDReader;
import com.ctc.wstx.shaded.msv_core.util.Util;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.FactoryImpl;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

public class DTDFactoryImpl
extends FactoryImpl {
    public DTDFactoryImpl(SAXParserFactory factory) {
        super(factory);
    }

    public DTDFactoryImpl() {
    }

    protected Grammar parse(InputSource is, GrammarReaderController controller) throws VerifierConfigurationException {
        return DTDReader.parse(is, controller);
    }

    protected Grammar parse(String source, GrammarReaderController controller) throws VerifierConfigurationException {
        return DTDReader.parse(Util.getInputSource(source), controller);
    }
}

