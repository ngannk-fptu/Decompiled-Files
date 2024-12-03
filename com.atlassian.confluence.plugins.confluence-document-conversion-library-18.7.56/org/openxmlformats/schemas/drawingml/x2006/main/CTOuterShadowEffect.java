/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.STFixedAngle
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.STFixedAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STRectAlignment;

public interface CTOuterShadowEffect
extends XmlObject {
    public static final DocumentFactory<CTOuterShadowEffect> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoutershadoweffect7b5dtype");
    public static final SchemaType type = Factory.getType();

    public CTScRgbColor getScrgbClr();

    public boolean isSetScrgbClr();

    public void setScrgbClr(CTScRgbColor var1);

    public CTScRgbColor addNewScrgbClr();

    public void unsetScrgbClr();

    public CTSRgbColor getSrgbClr();

    public boolean isSetSrgbClr();

    public void setSrgbClr(CTSRgbColor var1);

    public CTSRgbColor addNewSrgbClr();

    public void unsetSrgbClr();

    public CTHslColor getHslClr();

    public boolean isSetHslClr();

    public void setHslClr(CTHslColor var1);

    public CTHslColor addNewHslClr();

    public void unsetHslClr();

    public CTSystemColor getSysClr();

    public boolean isSetSysClr();

    public void setSysClr(CTSystemColor var1);

    public CTSystemColor addNewSysClr();

    public void unsetSysClr();

    public CTSchemeColor getSchemeClr();

    public boolean isSetSchemeClr();

    public void setSchemeClr(CTSchemeColor var1);

    public CTSchemeColor addNewSchemeClr();

    public void unsetSchemeClr();

    public CTPresetColor getPrstClr();

    public boolean isSetPrstClr();

    public void setPrstClr(CTPresetColor var1);

    public CTPresetColor addNewPrstClr();

    public void unsetPrstClr();

    public long getBlurRad();

    public STPositiveCoordinate xgetBlurRad();

    public boolean isSetBlurRad();

    public void setBlurRad(long var1);

    public void xsetBlurRad(STPositiveCoordinate var1);

    public void unsetBlurRad();

    public long getDist();

    public STPositiveCoordinate xgetDist();

    public boolean isSetDist();

    public void setDist(long var1);

    public void xsetDist(STPositiveCoordinate var1);

    public void unsetDist();

    public int getDir();

    public STPositiveFixedAngle xgetDir();

    public boolean isSetDir();

    public void setDir(int var1);

    public void xsetDir(STPositiveFixedAngle var1);

    public void unsetDir();

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

    public int getKx();

    public STFixedAngle xgetKx();

    public boolean isSetKx();

    public void setKx(int var1);

    public void xsetKx(STFixedAngle var1);

    public void unsetKx();

    public int getKy();

    public STFixedAngle xgetKy();

    public boolean isSetKy();

    public void setKy(int var1);

    public void xsetKy(STFixedAngle var1);

    public void unsetKy();

    public STRectAlignment.Enum getAlgn();

    public STRectAlignment xgetAlgn();

    public boolean isSetAlgn();

    public void setAlgn(STRectAlignment.Enum var1);

    public void xsetAlgn(STRectAlignment var1);

    public void unsetAlgn();

    public boolean getRotWithShape();

    public XmlBoolean xgetRotWithShape();

    public boolean isSetRotWithShape();

    public void setRotWithShape(boolean var1);

    public void xsetRotWithShape(XmlBoolean var1);

    public void unsetRotWithShape();
}

