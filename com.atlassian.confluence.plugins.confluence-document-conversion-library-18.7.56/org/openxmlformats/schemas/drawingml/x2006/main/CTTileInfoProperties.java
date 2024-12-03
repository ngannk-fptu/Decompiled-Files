/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STRectAlignment;
import org.openxmlformats.schemas.drawingml.x2006.main.STTileFlipMode;

public interface CTTileInfoProperties
extends XmlObject {
    public static final DocumentFactory<CTTileInfoProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttileinfoproperties2featype");
    public static final SchemaType type = Factory.getType();

    public Object getTx();

    public STCoordinate xgetTx();

    public boolean isSetTx();

    public void setTx(Object var1);

    public void xsetTx(STCoordinate var1);

    public void unsetTx();

    public Object getTy();

    public STCoordinate xgetTy();

    public boolean isSetTy();

    public void setTy(Object var1);

    public void xsetTy(STCoordinate var1);

    public void unsetTy();

    public Object getSx();

    public STPercentage xgetSx();

    public boolean isSetSx();

    public void setSx(Object var1);

    public void xsetSx(STPercentage var1);

    public void unsetSx();

    public Object getSy();

    public STPercentage xgetSy();

    public boolean isSetSy();

    public void setSy(Object var1);

    public void xsetSy(STPercentage var1);

    public void unsetSy();

    public STTileFlipMode.Enum getFlip();

    public STTileFlipMode xgetFlip();

    public boolean isSetFlip();

    public void setFlip(STTileFlipMode.Enum var1);

    public void xsetFlip(STTileFlipMode var1);

    public void unsetFlip();

    public STRectAlignment.Enum getAlgn();

    public STRectAlignment xgetAlgn();

    public boolean isSetAlgn();

    public void setAlgn(STRectAlignment.Enum var1);

    public void xsetAlgn(STRectAlignment var1);

    public void unsetAlgn();
}

