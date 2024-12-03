/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.ComplexType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface LocalComplexType
extends ComplexType {
    public static final DocumentFactory<LocalComplexType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "localcomplextype6494type");
    public static final SchemaType type = Factory.getType();
}

