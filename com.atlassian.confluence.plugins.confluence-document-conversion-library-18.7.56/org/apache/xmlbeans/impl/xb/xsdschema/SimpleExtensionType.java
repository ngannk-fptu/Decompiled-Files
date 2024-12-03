/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.ExtensionType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SimpleExtensionType
extends ExtensionType {
    public static final DocumentFactory<SimpleExtensionType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "simpleextensiontypee0detype");
    public static final SchemaType type = Factory.getType();
}

