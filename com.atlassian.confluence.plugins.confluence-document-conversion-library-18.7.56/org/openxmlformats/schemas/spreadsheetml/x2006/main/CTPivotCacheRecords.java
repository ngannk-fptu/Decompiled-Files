/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRecord
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRecord;

public interface CTPivotCacheRecords
extends XmlObject {
    public static final DocumentFactory<CTPivotCacheRecords> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpivotcacherecords5be1type");
    public static final SchemaType type = Factory.getType();

    public List<CTRecord> getRList();

    public CTRecord[] getRArray();

    public CTRecord getRArray(int var1);

    public int sizeOfRArray();

    public void setRArray(CTRecord[] var1);

    public void setRArray(int var1, CTRecord var2);

    public CTRecord insertNewR(int var1);

    public CTRecord addNewR();

    public void removeR(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}

