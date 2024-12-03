/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.TriggerType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface RowType
extends XmlObject {
    public static final DocumentFactory<RowType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "rowtype03d1type");
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

    public String getN();

    public XmlString xgetN();

    public boolean isSetN();

    public void setN(String var1);

    public void xsetN(XmlString var1);

    public void unsetN();

    public String getLocalName();

    public XmlString xgetLocalName();

    public boolean isSetLocalName();

    public void setLocalName(String var1);

    public void xsetLocalName(XmlString var1);

    public void unsetLocalName();

    public long getIX();

    public XmlUnsignedInt xgetIX();

    public boolean isSetIX();

    public void setIX(long var1);

    public void xsetIX(XmlUnsignedInt var1);

    public void unsetIX();

    public String getT();

    public XmlString xgetT();

    public boolean isSetT();

    public void setT(String var1);

    public void xsetT(XmlString var1);

    public void unsetT();

    public boolean getDel();

    public XmlBoolean xgetDel();

    public boolean isSetDel();

    public void setDel(boolean var1);

    public void xsetDel(XmlBoolean var1);

    public void unsetDel();
}

