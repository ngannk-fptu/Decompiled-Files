/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.visio.x2012.main.IconType
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.IconType;
import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;
import com.microsoft.schemas.office.visio.x2012.main.RelType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlUnsignedShort;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface MasterType
extends XmlObject {
    public static final DocumentFactory<MasterType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "mastertype2d97type");
    public static final SchemaType type = Factory.getType();

    public PageSheetType getPageSheet();

    public boolean isSetPageSheet();

    public void setPageSheet(PageSheetType var1);

    public PageSheetType addNewPageSheet();

    public void unsetPageSheet();

    public RelType getRel();

    public void setRel(RelType var1);

    public RelType addNewRel();

    public IconType getIcon();

    public boolean isSetIcon();

    public void setIcon(IconType var1);

    public IconType addNewIcon();

    public void unsetIcon();

    public long getID();

    public XmlUnsignedInt xgetID();

    public void setID(long var1);

    public void xsetID(XmlUnsignedInt var1);

    public String getBaseID();

    public XmlString xgetBaseID();

    public boolean isSetBaseID();

    public void setBaseID(String var1);

    public void xsetBaseID(XmlString var1);

    public void unsetBaseID();

    public String getUniqueID();

    public XmlString xgetUniqueID();

    public boolean isSetUniqueID();

    public void setUniqueID(String var1);

    public void xsetUniqueID(XmlString var1);

    public void unsetUniqueID();

    public boolean getMatchByName();

    public XmlBoolean xgetMatchByName();

    public boolean isSetMatchByName();

    public void setMatchByName(boolean var1);

    public void xsetMatchByName(XmlBoolean var1);

    public void unsetMatchByName();

    public String getName();

    public XmlString xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);

    public void unsetName();

    public String getNameU();

    public XmlString xgetNameU();

    public boolean isSetNameU();

    public void setNameU(String var1);

    public void xsetNameU(XmlString var1);

    public void unsetNameU();

    public boolean getIsCustomName();

    public XmlBoolean xgetIsCustomName();

    public boolean isSetIsCustomName();

    public void setIsCustomName(boolean var1);

    public void xsetIsCustomName(XmlBoolean var1);

    public void unsetIsCustomName();

    public boolean getIsCustomNameU();

    public XmlBoolean xgetIsCustomNameU();

    public boolean isSetIsCustomNameU();

    public void setIsCustomNameU(boolean var1);

    public void xsetIsCustomNameU(XmlBoolean var1);

    public void unsetIsCustomNameU();

    public int getIconSize();

    public XmlUnsignedShort xgetIconSize();

    public boolean isSetIconSize();

    public void setIconSize(int var1);

    public void xsetIconSize(XmlUnsignedShort var1);

    public void unsetIconSize();

    public int getPatternFlags();

    public XmlUnsignedShort xgetPatternFlags();

    public boolean isSetPatternFlags();

    public void setPatternFlags(int var1);

    public void xsetPatternFlags(XmlUnsignedShort var1);

    public void unsetPatternFlags();

    public String getPrompt();

    public XmlString xgetPrompt();

    public boolean isSetPrompt();

    public void setPrompt(String var1);

    public void xsetPrompt(XmlString var1);

    public void unsetPrompt();

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();

    public boolean getIconUpdate();

    public XmlBoolean xgetIconUpdate();

    public boolean isSetIconUpdate();

    public void setIconUpdate(boolean var1);

    public void xsetIconUpdate(XmlBoolean var1);

    public void unsetIconUpdate();

    public int getAlignName();

    public XmlUnsignedShort xgetAlignName();

    public boolean isSetAlignName();

    public void setAlignName(int var1);

    public void xsetAlignName(XmlUnsignedShort var1);

    public void unsetAlignName();

    public int getMasterType();

    public XmlUnsignedShort xgetMasterType();

    public boolean isSetMasterType();

    public void setMasterType(int var1);

    public void xsetMasterType(XmlUnsignedShort var1);

    public void unsetMasterType();
}

