/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface NoFixedFacet
extends Facet {
    public static final DocumentFactory<NoFixedFacet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "nofixedfacet250ftype");
    public static final SchemaType type = Factory.getType();
}

