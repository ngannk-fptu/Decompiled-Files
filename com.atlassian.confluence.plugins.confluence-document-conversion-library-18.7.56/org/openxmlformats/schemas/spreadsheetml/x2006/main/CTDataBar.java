/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

public interface CTDataBar
extends XmlObject {
    public static final DocumentFactory<CTDataBar> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdatabar4128type");
    public static final SchemaType type = Factory.getType();

    public List<CTCfvo> getCfvoList();

    public CTCfvo[] getCfvoArray();

    public CTCfvo getCfvoArray(int var1);

    public int sizeOfCfvoArray();

    public void setCfvoArray(CTCfvo[] var1);

    public void setCfvoArray(int var1, CTCfvo var2);

    public CTCfvo insertNewCfvo(int var1);

    public CTCfvo addNewCfvo();

    public void removeCfvo(int var1);

    public CTColor getColor();

    public void setColor(CTColor var1);

    public CTColor addNewColor();

    public long getMinLength();

    public XmlUnsignedInt xgetMinLength();

    public boolean isSetMinLength();

    public void setMinLength(long var1);

    public void xsetMinLength(XmlUnsignedInt var1);

    public void unsetMinLength();

    public long getMaxLength();

    public XmlUnsignedInt xgetMaxLength();

    public boolean isSetMaxLength();

    public void setMaxLength(long var1);

    public void xsetMaxLength(XmlUnsignedInt var1);

    public void unsetMaxLength();

    public boolean getShowValue();

    public XmlBoolean xgetShowValue();

    public boolean isSetShowValue();

    public void setShowValue(boolean var1);

    public void xsetShowValue(XmlBoolean var1);

    public void unsetShowValue();
}

