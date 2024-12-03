/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndLength;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndWidth;

public interface CTLineEndProperties
extends XmlObject {
    public static final DocumentFactory<CTLineEndProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlineendproperties8acbtype");
    public static final SchemaType type = Factory.getType();

    public STLineEndType.Enum getType();

    public STLineEndType xgetType();

    public boolean isSetType();

    public void setType(STLineEndType.Enum var1);

    public void xsetType(STLineEndType var1);

    public void unsetType();

    public STLineEndWidth.Enum getW();

    public STLineEndWidth xgetW();

    public boolean isSetW();

    public void setW(STLineEndWidth.Enum var1);

    public void xsetW(STLineEndWidth var1);

    public void unsetW();

    public STLineEndLength.Enum getLen();

    public STLineEndLength xgetLen();

    public boolean isSetLen();

    public void setLen(STLineEndLength.Enum var1);

    public void xsetLen(STLineEndLength var1);

    public void unsetLen();
}

