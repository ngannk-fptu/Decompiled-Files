/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.parsers.ObjectFactory;
import org.apache.xerces.parsers.XMLParser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public abstract class XMLGrammarParser
extends XMLParser {
    protected DTDDVFactory fDatatypeValidatorFactory;

    protected XMLGrammarParser(SymbolTable symbolTable) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
    }
}

