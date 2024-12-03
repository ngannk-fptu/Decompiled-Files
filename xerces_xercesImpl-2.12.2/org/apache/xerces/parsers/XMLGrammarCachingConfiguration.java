/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.IOException;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.parsers.XIncludeAwareParserConfiguration;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.SynchronizedSymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLGrammarCachingConfiguration
extends XIncludeAwareParserConfiguration {
    public static final int BIG_PRIME = 2039;
    protected static final SynchronizedSymbolTable fStaticSymbolTable = new SynchronizedSymbolTable(2039);
    protected static final XMLGrammarPoolImpl fStaticGrammarPool = new XMLGrammarPoolImpl();
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected XMLSchemaLoader fSchemaLoader;
    protected XMLDTDLoader fDTDLoader;

    public XMLGrammarCachingConfiguration() {
        this(fStaticSymbolTable, fStaticGrammarPool, null);
    }

    public XMLGrammarCachingConfiguration(SymbolTable symbolTable) {
        this(symbolTable, fStaticGrammarPool, null);
    }

    public XMLGrammarCachingConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public XMLGrammarCachingConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLGrammarPool, xMLComponentManager);
        this.fSchemaLoader = new XMLSchemaLoader(this.fSymbolTable);
        this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
        this.fDTDLoader = new XMLDTDLoader(this.fSymbolTable, this.fGrammarPool);
    }

    public void lockGrammarPool() {
        this.fGrammarPool.lockPool();
    }

    public void clearGrammarPool() {
        this.fGrammarPool.clear();
    }

    public void unlockGrammarPool() {
        this.fGrammarPool.unlockPool();
    }

    public Grammar parseGrammar(String string, String string2) throws XNIException, IOException {
        XMLInputSource xMLInputSource = new XMLInputSource(null, string2, null);
        return this.parseGrammar(string, xMLInputSource);
    }

    public Grammar parseGrammar(String string, XMLInputSource xMLInputSource) throws XNIException, IOException {
        if (string.equals("http://www.w3.org/2001/XMLSchema")) {
            return this.parseXMLSchema(xMLInputSource);
        }
        if (string.equals("http://www.w3.org/TR/REC-xml")) {
            return this.parseDTD(xMLInputSource);
        }
        return null;
    }

    @Override
    protected void checkFeature(String string) throws XMLConfigurationException {
        super.checkFeature(string);
    }

    @Override
    protected void checkProperty(String string) throws XMLConfigurationException {
        super.checkProperty(string);
    }

    SchemaGrammar parseXMLSchema(XMLInputSource xMLInputSource) throws IOException {
        XMLEntityResolver xMLEntityResolver = this.getEntityResolver();
        if (xMLEntityResolver != null) {
            this.fSchemaLoader.setEntityResolver(xMLEntityResolver);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        String string = "http://apache.org/xml/properties/";
        String string2 = string + "schema/external-schemaLocation";
        this.fSchemaLoader.setProperty(string2, this.getProperty(string2));
        string2 = string + "schema/external-noNamespaceSchemaLocation";
        this.fSchemaLoader.setProperty(string2, this.getProperty(string2));
        string2 = "http://java.sun.com/xml/jaxp/properties/schemaSource";
        this.fSchemaLoader.setProperty(string2, this.getProperty(string2));
        this.fSchemaLoader.setFeature(SCHEMA_FULL_CHECKING, this.getFeature(SCHEMA_FULL_CHECKING));
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fSchemaLoader.loadGrammar(xMLInputSource);
        if (schemaGrammar != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", new Grammar[]{schemaGrammar});
        }
        return schemaGrammar;
    }

    DTDGrammar parseDTD(XMLInputSource xMLInputSource) throws IOException {
        XMLEntityResolver xMLEntityResolver = this.getEntityResolver();
        if (xMLEntityResolver != null) {
            this.fDTDLoader.setEntityResolver(xMLEntityResolver);
        }
        this.fDTDLoader.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
        DTDGrammar dTDGrammar = (DTDGrammar)this.fDTDLoader.loadGrammar(xMLInputSource);
        if (dTDGrammar != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[]{dTDGrammar});
        }
        return dTDGrammar;
    }
}

