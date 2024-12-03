/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTBreak
extends XmlObject {
    public static final DocumentFactory<CTBreak> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbreak815etype");
    public static final SchemaType type = Factory.getType();

    public long getId();

    public XmlUnsignedInt xgetId();

    public boolean isSetId();

    public void setId(long var1);

    public void xsetId(XmlUnsignedInt var1);

    public void unsetId();

    public long getMin();

    public XmlUnsignedInt xgetMin();

    public boolean isSetMin();

    public void setMin(long var1);

    public void xsetMin(XmlUnsignedInt var1);

    public void unsetMin();

    public long getMax();

    public XmlUnsignedInt xgetMax();

    public boolean isSetMax();

    public void setMax(long var1);

    public void xsetMax(XmlUnsignedInt var1);

    public void unsetMax();

    public boolean getMan();

    public XmlBoolean xgetMan();

    public boolean isSetMan();

    public void setMan(boolean var1);

    public void xsetMan(XmlBoolean var1);

    public void unsetMan();

    public boolean getPt();

    public XmlBoolean xgetPt();

    public boolean isSetPt();

    public void setPt(boolean var1);

    public void xsetPt(XmlBoolean var1);

    public void unsetPt();
}

