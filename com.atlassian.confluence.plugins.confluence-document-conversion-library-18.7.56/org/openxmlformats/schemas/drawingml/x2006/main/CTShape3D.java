/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.main.CTBevel
 *  org.openxmlformats.schemas.drawingml.x2006.main.STPresetMaterialType
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBevel;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetMaterialType;

public interface CTShape3D
extends XmlObject {
    public static final DocumentFactory<CTShape3D> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshape3d6783type");
    public static final SchemaType type = Factory.getType();

    public CTBevel getBevelT();

    public boolean isSetBevelT();

    public void setBevelT(CTBevel var1);

    public CTBevel addNewBevelT();

    public void unsetBevelT();

    public CTBevel getBevelB();

    public boolean isSetBevelB();

    public void setBevelB(CTBevel var1);

    public CTBevel addNewBevelB();

    public void unsetBevelB();

    public CTColor getExtrusionClr();

    public boolean isSetExtrusionClr();

    public void setExtrusionClr(CTColor var1);

    public CTColor addNewExtrusionClr();

    public void unsetExtrusionClr();

    public CTColor getContourClr();

    public boolean isSetContourClr();

    public void setContourClr(CTColor var1);

    public CTColor addNewContourClr();

    public void unsetContourClr();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public Object getZ();

    public STCoordinate xgetZ();

    public boolean isSetZ();

    public void setZ(Object var1);

    public void xsetZ(STCoordinate var1);

    public void unsetZ();

    public long getExtrusionH();

    public STPositiveCoordinate xgetExtrusionH();

    public boolean isSetExtrusionH();

    public void setExtrusionH(long var1);

    public void xsetExtrusionH(STPositiveCoordinate var1);

    public void unsetExtrusionH();

    public long getContourW();

    public STPositiveCoordinate xgetContourW();

    public boolean isSetContourW();

    public void setContourW(long var1);

    public void xsetContourW(STPositiveCoordinate var1);

    public void unsetContourW();

    public STPresetMaterialType.Enum getPrstMaterial();

    public STPresetMaterialType xgetPrstMaterial();

    public boolean isSetPrstMaterial();

    public void setPrstMaterial(STPresetMaterialType.Enum var1);

    public void xsetPrstMaterial(STPresetMaterialType var1);

    public void unsetPrstMaterial();
}

