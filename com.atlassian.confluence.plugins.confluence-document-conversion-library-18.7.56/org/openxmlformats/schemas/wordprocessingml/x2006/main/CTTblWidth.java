/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMeasurementOrPercent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public interface CTTblWidth
extends XmlObject {
    public static final DocumentFactory<CTTblWidth> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblwidthec40type");
    public static final SchemaType type = Factory.getType();

    public Object getW();

    public STMeasurementOrPercent xgetW();

    public boolean isSetW();

    public void setW(Object var1);

    public void xsetW(STMeasurementOrPercent var1);

    public void unsetW();

    public STTblWidth.Enum getType();

    public STTblWidth xgetType();

    public boolean isSetType();

    public void setType(STTblWidth.Enum var1);

    public void xsetType(STTblWidth var1);

    public void unsetType();
}

