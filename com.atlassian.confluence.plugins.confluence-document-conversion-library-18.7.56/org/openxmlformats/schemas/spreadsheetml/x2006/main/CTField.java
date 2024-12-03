/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTField
extends XmlObject {
    public static final DocumentFactory<CTField> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfieldc999type");
    public static final SchemaType type = Factory.getType();

    public int getX();

    public XmlInt xgetX();

    public void setX(int var1);

    public void xsetX(XmlInt var1);
}

