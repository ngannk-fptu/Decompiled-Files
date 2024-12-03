/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.parsers.DTDConfiguration;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

public class StandardParserConfiguration
extends DTDConfiguration {
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String IGNORE_XSI_TYPE = "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl";
    protected static final String ID_IDREF_CHECKING = "http://apache.org/xml/features/validation/id-idref-checking";
    protected static final String UNPARSED_ENTITY_CHECKING = "http://apache.org/xml/features/validation/unparsed-entity-checking";
    protected static final String IDENTITY_CONSTRAINT_CHECKING = "http://apache.org/xml/features/validation/identity-constraint-checking";
    protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String ROOT_TYPE_DEF = "http://apache.org/xml/properties/validation/schema/root-type-definition";
    protected static final String ROOT_ELEMENT_DECL = "http://apache.org/xml/properties/validation/schema/root-element-declaration";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    protected XMLSchemaValidator fSchemaValidator;

    public StandardParserConfiguration() {
        this(null, null, null);
    }

    public StandardParserConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public StandardParserConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public StandardParserConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLGrammarPool, xMLComponentManager);
        String[] stringArray = new String[]{NORMALIZE_DATA, SCHEMA_ELEMENT_DEFAULT, SCHEMA_AUGMENT_PSVI, GENERATE_SYNTHETIC_ANNOTATIONS, VALIDATE_ANNOTATIONS, HONOUR_ALL_SCHEMALOCATIONS, NAMESPACE_GROWTH, TOLERATE_DUPLICATES, XMLSCHEMA_VALIDATION, XMLSCHEMA_FULL_CHECKING, IGNORE_XSI_TYPE, ID_IDREF_CHECKING, IDENTITY_CONSTRAINT_CHECKING, UNPARSED_ENTITY_CHECKING};
        this.addRecognizedFeatures(stringArray);
        this.setFeature(SCHEMA_ELEMENT_DEFAULT, true);
        this.setFeature(NORMALIZE_DATA, true);
        this.setFeature(SCHEMA_AUGMENT_PSVI, true);
        this.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS, false);
        this.setFeature(VALIDATE_ANNOTATIONS, false);
        this.setFeature(HONOUR_ALL_SCHEMALOCATIONS, false);
        this.setFeature(NAMESPACE_GROWTH, false);
        this.setFeature(TOLERATE_DUPLICATES, false);
        this.setFeature(IGNORE_XSI_TYPE, false);
        this.setFeature(ID_IDREF_CHECKING, true);
        this.setFeature(IDENTITY_CONSTRAINT_CHECKING, true);
        this.setFeature(UNPARSED_ENTITY_CHECKING, true);
        String[] stringArray2 = new String[]{SCHEMA_LOCATION, SCHEMA_NONS_LOCATION, ROOT_TYPE_DEF, ROOT_ELEMENT_DECL, SCHEMA_DV_FACTORY};
        this.addRecognizedProperties(stringArray2);
    }

    @Override
    protected void configurePipeline() {
        super.configurePipeline();
        if (this.getFeature(XMLSCHEMA_VALIDATION)) {
            if (this.fSchemaValidator == null) {
                this.fSchemaValidator = new XMLSchemaValidator();
                this.fProperties.put(SCHEMA_VALIDATOR, this.fSchemaValidator);
                this.addComponent(this.fSchemaValidator);
                if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
                    XSMessageFormatter xSMessageFormatter = new XSMessageFormatter();
                    this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", xSMessageFormatter);
                }
            }
            this.fLastComponent = this.fSchemaValidator;
            this.fNamespaceBinder.setDocumentHandler(this.fSchemaValidator);
            this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
            this.fSchemaValidator.setDocumentSource(this.fNamespaceBinder);
        }
    }

    @Override
    protected void checkFeature(String string) throws XMLConfigurationException {
        if (string.startsWith("http://apache.org/xml/features/")) {
            int n = string.length() - "http://apache.org/xml/features/".length();
            if (n == "validation/schema".length() && string.endsWith("validation/schema")) {
                return;
            }
            if (n == "validation/schema-full-checking".length() && string.endsWith("validation/schema-full-checking")) {
                return;
            }
            if (n == "validation/schema/normalized-value".length() && string.endsWith("validation/schema/normalized-value")) {
                return;
            }
            if (n == "validation/schema/element-default".length() && string.endsWith("validation/schema/element-default")) {
                return;
            }
        }
        super.checkFeature(string);
    }

    @Override
    protected void checkProperty(String string) throws XMLConfigurationException {
        int n;
        if (string.startsWith("http://apache.org/xml/properties/")) {
            n = string.length() - "http://apache.org/xml/properties/".length();
            if (n == "schema/external-schemaLocation".length() && string.endsWith("schema/external-schemaLocation")) {
                return;
            }
            if (n == "schema/external-noNamespaceSchemaLocation".length() && string.endsWith("schema/external-noNamespaceSchemaLocation")) {
                return;
            }
        }
        if (string.startsWith("http://java.sun.com/xml/jaxp/properties/") && (n = string.length() - "http://java.sun.com/xml/jaxp/properties/".length()) == "schemaSource".length() && string.endsWith("schemaSource")) {
            return;
        }
        super.checkProperty(string);
    }
}

