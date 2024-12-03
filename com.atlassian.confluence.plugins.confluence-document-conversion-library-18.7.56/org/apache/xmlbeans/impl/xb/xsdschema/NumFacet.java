/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface NumFacet
extends Facet {
    public static final DocumentFactory<NumFacet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "numfacet93a2type");
    public static final SchemaType type = Factory.getType();
}

