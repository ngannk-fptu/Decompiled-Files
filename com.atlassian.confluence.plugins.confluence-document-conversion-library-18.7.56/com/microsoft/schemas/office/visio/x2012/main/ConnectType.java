/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface ConnectType
extends XmlObject {
    public static final DocumentFactory<ConnectType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "connecttypeea41type");
    public static final SchemaType type = Factory.getType();

    public long getFromSheet();

    public XmlUnsignedInt xgetFromSheet();

    public void setFromSheet(long var1);

    public void xsetFromSheet(XmlUnsignedInt var1);

    public String getFromCell();

    public XmlString xgetFromCell();

    public boolean isSetFromCell();

    public void setFromCell(String var1);

    public void xsetFromCell(XmlString var1);

    public void unsetFromCell();

    public int getFromPart();

    public XmlInt xgetFromPart();

    public boolean isSetFromPart();

    public void setFromPart(int var1);

    public void xsetFromPart(XmlInt var1);

    public void unsetFromPart();

    public long getToSheet();

    public XmlUnsignedInt xgetToSheet();

    public void setToSheet(long var1);

    public void xsetToSheet(XmlUnsignedInt var1);

    public String getToCell();

    public XmlString xgetToCell();

    public boolean isSetToCell();

    public void setToCell(String var1);

    public void xsetToCell(XmlString var1);

    public void unsetToCell();

    public int getToPart();

    public XmlInt xgetToPart();

    public boolean isSetToPart();

    public void setToPart(int var1);

    public void xsetToPart(XmlInt var1);

    public void unsetToPart();
}

