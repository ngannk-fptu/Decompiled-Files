/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;

public interface CTDuotoneEffect
extends XmlObject {
    public static final DocumentFactory<CTDuotoneEffect> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctduotoneeffectae52type");
    public static final SchemaType type = Factory.getType();

    public List<CTScRgbColor> getScrgbClrList();

    public CTScRgbColor[] getScrgbClrArray();

    public CTScRgbColor getScrgbClrArray(int var1);

    public int sizeOfScrgbClrArray();

    public void setScrgbClrArray(CTScRgbColor[] var1);

    public void setScrgbClrArray(int var1, CTScRgbColor var2);

    public CTScRgbColor insertNewScrgbClr(int var1);

    public CTScRgbColor addNewScrgbClr();

    public void removeScrgbClr(int var1);

    public List<CTSRgbColor> getSrgbClrList();

    public CTSRgbColor[] getSrgbClrArray();

    public CTSRgbColor getSrgbClrArray(int var1);

    public int sizeOfSrgbClrArray();

    public void setSrgbClrArray(CTSRgbColor[] var1);

    public void setSrgbClrArray(int var1, CTSRgbColor var2);

    public CTSRgbColor insertNewSrgbClr(int var1);

    public CTSRgbColor addNewSrgbClr();

    public void removeSrgbClr(int var1);

    public List<CTHslColor> getHslClrList();

    public CTHslColor[] getHslClrArray();

    public CTHslColor getHslClrArray(int var1);

    public int sizeOfHslClrArray();

    public void setHslClrArray(CTHslColor[] var1);

    public void setHslClrArray(int var1, CTHslColor var2);

    public CTHslColor insertNewHslClr(int var1);

    public CTHslColor addNewHslClr();

    public void removeHslClr(int var1);

    public List<CTSystemColor> getSysClrList();

    public CTSystemColor[] getSysClrArray();

    public CTSystemColor getSysClrArray(int var1);

    public int sizeOfSysClrArray();

    public void setSysClrArray(CTSystemColor[] var1);

    public void setSysClrArray(int var1, CTSystemColor var2);

    public CTSystemColor insertNewSysClr(int var1);

    public CTSystemColor addNewSysClr();

    public void removeSysClr(int var1);

    public List<CTSchemeColor> getSchemeClrList();

    public CTSchemeColor[] getSchemeClrArray();

    public CTSchemeColor getSchemeClrArray(int var1);

    public int sizeOfSchemeClrArray();

    public void setSchemeClrArray(CTSchemeColor[] var1);

    public void setSchemeClrArray(int var1, CTSchemeColor var2);

    public CTSchemeColor insertNewSchemeClr(int var1);

    public CTSchemeColor addNewSchemeClr();

    public void removeSchemeClr(int var1);

    public List<CTPresetColor> getPrstClrList();

    public CTPresetColor[] getPrstClrArray();

    public CTPresetColor getPrstClrArray(int var1);

    public int sizeOfPrstClrArray();

    public void setPrstClrArray(CTPresetColor[] var1);

    public void setPrstClrArray(int var1, CTPresetColor var2);

    public CTPresetColor insertNewPrstClr(int var1);

    public CTPresetColor addNewPrstClr();

    public void removePrstClr(int var1);
}

