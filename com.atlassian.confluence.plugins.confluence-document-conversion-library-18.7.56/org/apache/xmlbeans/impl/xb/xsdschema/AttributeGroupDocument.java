/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface AttributeGroupDocument
extends XmlObject {
    public static final DocumentFactory<AttributeGroupDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "attributegroup4520doctype");
    public static final SchemaType type = Factory.getType();

    public NamedAttributeGroup getAttributeGroup();

    public void setAttributeGroup(NamedAttributeGroup var1);

    public NamedAttributeGroup addNewAttributeGroup();
}

