/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.schema;

import javax.wsdl.extensions.schema.SchemaReference;

public interface SchemaImport
extends SchemaReference {
    public String getNamespaceURI();

    public void setNamespaceURI(String var1);
}

