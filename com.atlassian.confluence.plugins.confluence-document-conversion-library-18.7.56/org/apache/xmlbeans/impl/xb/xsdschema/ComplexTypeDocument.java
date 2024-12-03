/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ComplexTypeDocument
extends XmlObject {
    public static final DocumentFactory<ComplexTypeDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "complextype83cbdoctype");
    public static final SchemaType type = Factory.getType();

    public TopLevelComplexType getComplexType();

    public void setComplexType(TopLevelComplexType var1);

    public TopLevelComplexType addNewComplexType();
}

