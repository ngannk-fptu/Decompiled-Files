/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import com.microsoft.schemas.office.visio.x2012.main.TriggerType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;

public interface SheetType
extends XmlObject {
    public static final AbstractDocumentFactory<SheetType> Factory = new AbstractDocumentFactory(TypeSystemHolder.typeSystem, "sheettype25actype");
    public static final SchemaType type = Factory.getType();

    public List<CellType> getCellList();

    public CellType[] getCellArray();

    public CellType getCellArray(int var1);

    public int sizeOfCellArray();

    public void setCellArray(CellType[] var1);

    public void setCellArray(int var1, CellType var2);

    public CellType insertNewCell(int var1);

    public CellType addNewCell();

    public void removeCell(int var1);

    public List<TriggerType> getTriggerList();

    public TriggerType[] getTriggerArray();

    public TriggerType getTriggerArray(int var1);

    public int sizeOfTriggerArray();

    public void setTriggerArray(TriggerType[] var1);

    public void setTriggerArray(int var1, TriggerType var2);

    public TriggerType insertNewTrigger(int var1);

    public TriggerType addNewTrigger();

    public void removeTrigger(int var1);

    public List<SectionType> getSectionList();

    public SectionType[] getSectionArray();

    public SectionType getSectionArray(int var1);

    public int sizeOfSectionArray();

    public void setSectionArray(SectionType[] var1);

    public void setSectionArray(int var1, SectionType var2);

    public SectionType insertNewSection(int var1);

    public SectionType addNewSection();

    public void removeSection(int var1);

    public long getLineStyle();

    public XmlUnsignedInt xgetLineStyle();

    public boolean isSetLineStyle();

    public void setLineStyle(long var1);

    public void xsetLineStyle(XmlUnsignedInt var1);

    public void unsetLineStyle();

    public long getFillStyle();

    public XmlUnsignedInt xgetFillStyle();

    public boolean isSetFillStyle();

    public void setFillStyle(long var1);

    public void xsetFillStyle(XmlUnsignedInt var1);

    public void unsetFillStyle();

    public long getTextStyle();

    public XmlUnsignedInt xgetTextStyle();

    public boolean isSetTextStyle();

    public void setTextStyle(long var1);

    public void xsetTextStyle(XmlUnsignedInt var1);

    public void unsetTextStyle();
}

