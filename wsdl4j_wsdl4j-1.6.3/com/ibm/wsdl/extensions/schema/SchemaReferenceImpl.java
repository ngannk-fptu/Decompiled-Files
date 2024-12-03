/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.schema;

import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;

public class SchemaReferenceImpl
implements SchemaReference {
    public static final long serialVersionUID = 1L;
    private String id = null;
    private String schemaLocation = null;
    private Schema referencedSchema = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchemaLocationURI() {
        return this.schemaLocation;
    }

    public void setSchemaLocationURI(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public Schema getReferencedSchema() {
        return this.referencedSchema;
    }

    public void setReferencedSchema(Schema referencedSchema) {
        this.referencedSchema = referencedSchema;
    }
}

