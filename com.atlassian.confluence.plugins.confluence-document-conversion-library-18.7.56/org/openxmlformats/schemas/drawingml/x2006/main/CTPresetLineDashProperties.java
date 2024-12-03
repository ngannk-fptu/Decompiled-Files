/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;

public interface CTPresetLineDashProperties
extends XmlObject {
    public static final DocumentFactory<CTPresetLineDashProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpresetlinedashproperties4553type");
    public static final SchemaType type = Factory.getType();

    public STPresetLineDashVal.Enum getVal();

    public STPresetLineDashVal xgetVal();

    public boolean isSetVal();

    public void setVal(STPresetLineDashVal.Enum var1);

    public void xsetVal(STPresetLineDashVal var1);

    public void unsetVal();
}

