/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStretchInfoProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;

public interface CTBlipFillProperties
extends XmlObject {
    public static final DocumentFactory<CTBlipFillProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctblipfillproperties0382type");
    public static final SchemaType type = Factory.getType();

    public CTBlip getBlip();

    public boolean isSetBlip();

    public void setBlip(CTBlip var1);

    public CTBlip addNewBlip();

    public void unsetBlip();

    public CTRelativeRect getSrcRect();

    public boolean isSetSrcRect();

    public void setSrcRect(CTRelativeRect var1);

    public CTRelativeRect addNewSrcRect();

    public void unsetSrcRect();

    public CTTileInfoProperties getTile();

    public boolean isSetTile();

    public void setTile(CTTileInfoProperties var1);

    public CTTileInfoProperties addNewTile();

    public void unsetTile();

    public CTStretchInfoProperties getStretch();

    public boolean isSetStretch();

    public void setStretch(CTStretchInfoProperties var1);

    public CTStretchInfoProperties addNewStretch();

    public void unsetStretch();

    public long getDpi();

    public XmlUnsignedInt xgetDpi();

    public boolean isSetDpi();

    public void setDpi(long var1);

    public void xsetDpi(XmlUnsignedInt var1);

    public void unsetDpi();

    public boolean getRotWithShape();

    public XmlBoolean xgetRotWithShape();

    public boolean isSetRotWithShape();

    public void setRotWithShape(boolean var1);

    public void xsetRotWithShape(XmlBoolean var1);

    public void unsetRotWithShape();
}

