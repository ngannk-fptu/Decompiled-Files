/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.util.Vector;
import org.apache.xerces.dom.ASModelImpl;
import org.apache.xerces.dom3.as.ASModel;
import org.apache.xerces.dom3.as.DOMASBuilder;
import org.apache.xerces.dom3.as.DOMASException;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.parsers.DOMParserImpl;
import org.apache.xerces.parsers.XMLGrammarCachingConfiguration;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.ls.LSInput;

public class DOMASBuilderImpl
extends DOMParserImpl
implements DOMASBuilder {
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected XSGrammarBucket fGrammarBucket;
    protected ASModelImpl fAbstractSchema;

    public DOMASBuilderImpl() {
        super(new XMLGrammarCachingConfiguration());
    }

    public DOMASBuilderImpl(XMLGrammarCachingConfiguration xMLGrammarCachingConfiguration) {
        super(xMLGrammarCachingConfiguration);
    }

    public DOMASBuilderImpl(SymbolTable symbolTable) {
        super(new XMLGrammarCachingConfiguration(symbolTable));
    }

    public DOMASBuilderImpl(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        super(new XMLGrammarCachingConfiguration(symbolTable, xMLGrammarPool));
    }

    @Override
    public ASModel getAbstractSchema() {
        return this.fAbstractSchema;
    }

    @Override
    public void setAbstractSchema(ASModel aSModel) {
        this.fAbstractSchema = (ASModelImpl)aSModel;
        XMLGrammarPool xMLGrammarPool = (XMLGrammarPool)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/grammar-pool");
        if (xMLGrammarPool == null) {
            xMLGrammarPool = new XMLGrammarPoolImpl();
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xMLGrammarPool);
        }
        if (this.fAbstractSchema != null) {
            this.initGrammarPool(this.fAbstractSchema, xMLGrammarPool);
        }
    }

    @Override
    public ASModel parseASURI(String string) throws DOMASException, Exception {
        XMLInputSource xMLInputSource = new XMLInputSource(null, string, null);
        return this.parseASInputSource(xMLInputSource);
    }

    @Override
    public ASModel parseASInputSource(LSInput lSInput) throws DOMASException, Exception {
        XMLInputSource xMLInputSource = this.dom2xmlInputSource(lSInput);
        try {
            return this.parseASInputSource(xMLInputSource);
        }
        catch (XNIException xNIException) {
            Exception exception = xNIException.getException();
            throw exception;
        }
    }

    ASModel parseASInputSource(XMLInputSource xMLInputSource) throws Exception {
        if (this.fGrammarBucket == null) {
            this.fGrammarBucket = new XSGrammarBucket();
        }
        this.initGrammarBucket();
        XMLGrammarCachingConfiguration xMLGrammarCachingConfiguration = (XMLGrammarCachingConfiguration)this.fConfiguration;
        xMLGrammarCachingConfiguration.lockGrammarPool();
        SchemaGrammar schemaGrammar = xMLGrammarCachingConfiguration.parseXMLSchema(xMLInputSource);
        xMLGrammarCachingConfiguration.unlockGrammarPool();
        ASModelImpl aSModelImpl = null;
        if (schemaGrammar != null) {
            aSModelImpl = new ASModelImpl();
            this.fGrammarBucket.putGrammar(schemaGrammar, true);
            this.addGrammars(aSModelImpl, this.fGrammarBucket);
        }
        return aSModelImpl;
    }

    private void initGrammarBucket() {
        this.fGrammarBucket.reset();
        if (this.fAbstractSchema != null) {
            this.initGrammarBucketRecurse(this.fAbstractSchema);
        }
    }

    private void initGrammarBucketRecurse(ASModelImpl aSModelImpl) {
        if (aSModelImpl.getGrammar() != null) {
            this.fGrammarBucket.putGrammar(aSModelImpl.getGrammar());
        }
        for (int i = 0; i < aSModelImpl.getInternalASModels().size(); ++i) {
            ASModelImpl aSModelImpl2 = (ASModelImpl)aSModelImpl.getInternalASModels().elementAt(i);
            this.initGrammarBucketRecurse(aSModelImpl2);
        }
    }

    private void addGrammars(ASModelImpl aSModelImpl, XSGrammarBucket xSGrammarBucket) {
        SchemaGrammar[] schemaGrammarArray = xSGrammarBucket.getGrammars();
        for (int i = 0; i < schemaGrammarArray.length; ++i) {
            ASModelImpl aSModelImpl2 = new ASModelImpl();
            aSModelImpl2.setGrammar(schemaGrammarArray[i]);
            aSModelImpl.addASModel(aSModelImpl2);
        }
    }

    private void initGrammarPool(ASModelImpl aSModelImpl, XMLGrammarPool xMLGrammarPool) {
        Grammar[] grammarArray = new Grammar[1];
        grammarArray[0] = aSModelImpl.getGrammar();
        if (grammarArray[0] != null) {
            xMLGrammarPool.cacheGrammars(grammarArray[0].getGrammarDescription().getGrammarType(), grammarArray);
        }
        Vector vector = aSModelImpl.getInternalASModels();
        for (int i = 0; i < vector.size(); ++i) {
            this.initGrammarPool((ASModelImpl)vector.elementAt(i), xMLGrammarPool);
        }
    }
}

