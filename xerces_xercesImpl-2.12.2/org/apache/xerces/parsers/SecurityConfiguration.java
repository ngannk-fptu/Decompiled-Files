/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;

public class SecurityConfiguration
extends XIncludeAwareParserConfiguration {
    protected static final String SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";

    public SecurityConfiguration() {
        this(null, null, null);
    }

    public SecurityConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public SecurityConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public SecurityConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLGrammarPool, xMLComponentManager);
        this.setProperty(SECURITY_MANAGER_PROPERTY, new SecurityManager());
    }
}

