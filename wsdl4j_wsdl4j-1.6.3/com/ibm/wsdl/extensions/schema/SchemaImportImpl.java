/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.extensions.schema;

import com.ibm.wsdl.extensions.schema.SchemaReferenceImpl;
import javax.wsdl.extensions.schema.SchemaImport;

public class SchemaImportImpl
extends SchemaReferenceImpl
implements SchemaImport {
    public static final long serialVersionUID = 1L;
    private String namespace = null;

    public String getNamespaceURI() {
        return this.namespace;
    }

    public void setNamespaceURI(String namespace) {
        this.namespace = namespace;
    }
}

