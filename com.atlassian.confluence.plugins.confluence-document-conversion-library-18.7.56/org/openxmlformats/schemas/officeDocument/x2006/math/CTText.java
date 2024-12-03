/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;

public interface CTText
extends STString {
    public static final DocumentFactory<CTText> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttext8965type");
    public static final SchemaType type = Factory.getType();

    public SpaceAttribute.Space.Enum getSpace();

    public SpaceAttribute.Space xgetSpace();

    public boolean isSetSpace();

    public void setSpace(SpaceAttribute.Space.Enum var1);

    public void xsetSpace(SpaceAttribute.Space var1);

    public void unsetSpace();
}

