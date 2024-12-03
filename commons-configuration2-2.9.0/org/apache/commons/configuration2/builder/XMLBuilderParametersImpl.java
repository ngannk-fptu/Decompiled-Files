/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import org.apache.commons.configuration2.builder.HierarchicalBuilderParametersImpl;
import org.apache.commons.configuration2.builder.XMLBuilderProperties;
import org.xml.sax.EntityResolver;

public class XMLBuilderParametersImpl
extends HierarchicalBuilderParametersImpl
implements XMLBuilderProperties<XMLBuilderParametersImpl> {
    private static final String PROP_ENTITY_RESOLVER = "entityResolver";
    private static final String PROP_DOCUMENT_BUILDER = "documentBuilder";
    private static final String PROP_PUBLIC_ID = "publicID";
    private static final String PROP_SYSTEM_ID = "systemID";
    private static final String PROP_VALIDATING = "validating";
    private static final String PROP_SCHEMA_VALIDATION = "schemaValidation";

    @Override
    public void inheritFrom(Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, PROP_DOCUMENT_BUILDER, PROP_ENTITY_RESOLVER, PROP_SCHEMA_VALIDATION, PROP_VALIDATING);
    }

    @Override
    public XMLBuilderParametersImpl setDocumentBuilder(DocumentBuilder docBuilder) {
        this.storeProperty(PROP_DOCUMENT_BUILDER, docBuilder);
        return this;
    }

    @Override
    public XMLBuilderParametersImpl setEntityResolver(EntityResolver resolver) {
        this.storeProperty(PROP_ENTITY_RESOLVER, resolver);
        return this;
    }

    public EntityResolver getEntityResolver() {
        return (EntityResolver)this.fetchProperty(PROP_ENTITY_RESOLVER);
    }

    @Override
    public XMLBuilderParametersImpl setPublicID(String pubID) {
        this.storeProperty(PROP_PUBLIC_ID, pubID);
        return this;
    }

    @Override
    public XMLBuilderParametersImpl setSystemID(String sysID) {
        this.storeProperty(PROP_SYSTEM_ID, sysID);
        return this;
    }

    @Override
    public XMLBuilderParametersImpl setValidating(boolean f) {
        this.storeProperty(PROP_VALIDATING, f);
        return this;
    }

    @Override
    public XMLBuilderParametersImpl setSchemaValidation(boolean f) {
        this.storeProperty(PROP_SCHEMA_VALIDATION, f);
        return this;
    }
}

