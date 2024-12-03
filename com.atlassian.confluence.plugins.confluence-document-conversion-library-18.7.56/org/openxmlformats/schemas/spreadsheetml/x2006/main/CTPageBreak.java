/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBreak;

public interface CTPageBreak
extends XmlObject {
    public static final DocumentFactory<CTPageBreak> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagebreakeb4ftype");
    public static final SchemaType type = Factory.getType();

    public List<CTBreak> getBrkList();

    public CTBreak[] getBrkArray();

    public CTBreak getBrkArray(int var1);

    public int sizeOfBrkArray();

    public void setBrkArray(CTBreak[] var1);

    public void setBrkArray(int var1, CTBreak var2);

    public CTBreak insertNewBrk(int var1);

    public CTBreak addNewBrk();

    public void removeBrk(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();

    public long getManualBreakCount();

    public XmlUnsignedInt xgetManualBreakCount();

    public boolean isSetManualBreakCount();

    public void setManualBreakCount(long var1);

    public void xsetManualBreakCount(XmlUnsignedInt var1);

    public void unsetManualBreakCount();
}

