/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface EnumerationDocument
extends XmlObject {
    public static final DocumentFactory<EnumerationDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "enumeration052edoctype");
    public static final SchemaType type = Factory.getType();

    public NoFixedFacet getEnumeration();

    public void setEnumeration(NoFixedFacet var1);

    public NoFixedFacet addNewEnumeration();
}

