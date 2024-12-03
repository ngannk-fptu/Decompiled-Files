/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.SimpleType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface LocalSimpleType
extends SimpleType {
    public static final DocumentFactory<LocalSimpleType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "localsimpletype410etype");
    public static final SchemaType type = Factory.getType();
}

