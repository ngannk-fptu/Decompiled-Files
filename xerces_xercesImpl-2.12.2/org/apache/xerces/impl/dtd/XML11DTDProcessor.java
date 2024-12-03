/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.XML11DTDScannerImpl;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLEntityResolver;

public class XML11DTDProcessor
extends XMLDTDLoader {
    public XML11DTDProcessor() {
    }

    public XML11DTDProcessor(SymbolTable symbolTable) {
        super(symbolTable);
    }

    public XML11DTDProcessor(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        super(symbolTable, xMLGrammarPool);
    }

    XML11DTDProcessor(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLErrorReporter xMLErrorReporter, XMLEntityResolver xMLEntityResolver) {
        super(symbolTable, xMLGrammarPool, xMLErrorReporter, xMLEntityResolver);
    }

    @Override
    protected boolean isValidNmtoken(String string) {
        return XML11Char.isXML11ValidNmtoken(string);
    }

    @Override
    protected boolean isValidName(String string) {
        return XML11Char.isXML11ValidName(string);
    }

    @Override
    protected XMLDTDScannerImpl createDTDScanner(SymbolTable symbolTable, XMLErrorReporter xMLErrorReporter, XMLEntityManager xMLEntityManager) {
        return new XML11DTDScannerImpl(symbolTable, xMLErrorReporter, xMLEntityManager);
    }

    @Override
    protected short getScannerVersion() {
        return 2;
    }
}

