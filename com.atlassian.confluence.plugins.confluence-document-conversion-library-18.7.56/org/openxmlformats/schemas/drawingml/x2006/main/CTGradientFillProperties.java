/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLinearShadeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPathShadeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.STTileFlipMode;

public interface CTGradientFillProperties
extends XmlObject {
    public static final DocumentFactory<CTGradientFillProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgradientfillproperties81c1type");
    public static final SchemaType type = Factory.getType();

    public CTGradientStopList getGsLst();

    public boolean isSetGsLst();

    public void setGsLst(CTGradientStopList var1);

    public CTGradientStopList addNewGsLst();

    public void unsetGsLst();

    public CTLinearShadeProperties getLin();

    public boolean isSetLin();

    public void setLin(CTLinearShadeProperties var1);

    public CTLinearShadeProperties addNewLin();

    public void unsetLin();

    public CTPathShadeProperties getPath();

    public boolean isSetPath();

    public void setPath(CTPathShadeProperties var1);

    public CTPathShadeProperties addNewPath();

    public void unsetPath();

    public CTRelativeRect getTileRect();

    public boolean isSetTileRect();

    public void setTileRect(CTRelativeRect var1);

    public CTRelativeRect addNewTileRect();

    public void unsetTileRect();

    public STTileFlipMode.Enum getFlip();

    public STTileFlipMode xgetFlip();

    public boolean isSetFlip();

    public void setFlip(STTileFlipMode.Enum var1);

    public void xsetFlip(STTileFlipMode var1);

    public void unsetFlip();

    public boolean getRotWithShape();

    public XmlBoolean xgetRotWithShape();

    public boolean isSetRotWithShape();

    public void setRotWithShape(boolean var1);

    public void xsetRotWithShape(XmlBoolean var1);

    public void unsetRotWithShape();
}

