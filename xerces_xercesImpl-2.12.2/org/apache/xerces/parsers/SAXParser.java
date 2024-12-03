/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.parsers.ObjectFactory;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public class SAXParser
extends AbstractSAXParser {
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://apache.org/xml/features/scanner/notify-builtin-refs"};
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool"};

    public SAXParser(XMLParserConfiguration xMLParserConfiguration) {
        super(xMLParserConfiguration);
    }

    public SAXParser() {
        this(null, null);
    }

    public SAXParser(SymbolTable symbolTable) {
        this(symbolTable, null);
    }

    public SAXParser(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
        this.fConfiguration.setFeature(NOTIFY_BUILTIN_REFS, true);
        this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
        }
        if (xMLGrammarPool != null) {
            this.fConfiguration.setProperty(XMLGRAMMAR_POOL, xMLGrammarPool);
        }
    }
}

