/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STCalendarType;

public interface CTCalendarType
extends XmlObject {
    public static final DocumentFactory<CTCalendarType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcalendartyped1d0type");
    public static final SchemaType type = Factory.getType();

    public STCalendarType.Enum getVal();

    public STCalendarType xgetVal();

    public boolean isSetVal();

    public void setVal(STCalendarType.Enum var1);

    public void xsetVal(STCalendarType var1);

    public void unsetVal();
}

