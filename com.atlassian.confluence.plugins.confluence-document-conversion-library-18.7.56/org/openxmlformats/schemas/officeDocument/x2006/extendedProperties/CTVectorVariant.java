/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVector;

public interface CTVectorVariant
extends XmlObject {
    public static final DocumentFactory<CTVectorVariant> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctvectorvariant9d75type");
    public static final SchemaType type = Factory.getType();

    public CTVector getVector();

    public void setVector(CTVector var1);

    public CTVector addNewVector();
}

