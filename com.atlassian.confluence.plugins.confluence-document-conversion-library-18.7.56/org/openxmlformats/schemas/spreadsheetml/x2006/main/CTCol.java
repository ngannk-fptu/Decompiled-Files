/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTCol
extends XmlObject {
    public static final DocumentFactory<CTCol> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcola95ftype");
    public static final SchemaType type = Factory.getType();

    public long getMin();

    public XmlUnsignedInt xgetMin();

    public void setMin(long var1);

    public void xsetMin(XmlUnsignedInt var1);

    public long getMax();

    public XmlUnsignedInt xgetMax();

    public void setMax(long var1);

    public void xsetMax(XmlUnsignedInt var1);

    public double getWidth();

    public XmlDouble xgetWidth();

    public boolean isSetWidth();

    public void setWidth(double var1);

    public void xsetWidth(XmlDouble var1);

    public void unsetWidth();

    public long getStyle();

    public XmlUnsignedInt xgetStyle();

    public boolean isSetStyle();

    public void setStyle(long var1);

    public void xsetStyle(XmlUnsignedInt var1);

    public void unsetStyle();

    public boolean getHidden();

    public XmlBoolean xgetHidden();

    public boolean isSetHidden();

    public void setHidden(boolean var1);

    public void xsetHidden(XmlBoolean var1);

    public void unsetHidden();

    public boolean getBestFit();

    public XmlBoolean xgetBestFit();

    public boolean isSetBestFit();

    public void setBestFit(boolean var1);

    public void xsetBestFit(XmlBoolean var1);

    public void unsetBestFit();

    public boolean getCustomWidth();

    public XmlBoolean xgetCustomWidth();

    public boolean isSetCustomWidth();

    public void setCustomWidth(boolean var1);

    public void xsetCustomWidth(XmlBoolean var1);

    public void unsetCustomWidth();

    public boolean getPhonetic();

    public XmlBoolean xgetPhonetic();

    public boolean isSetPhonetic();

    public void setPhonetic(boolean var1);

    public void xsetPhonetic(XmlBoolean var1);

    public void unsetPhonetic();

    public short getOutlineLevel();

    public XmlUnsignedByte xgetOutlineLevel();

    public boolean isSetOutlineLevel();

    public void setOutlineLevel(short var1);

    public void xsetOutlineLevel(XmlUnsignedByte var1);

    public void unsetOutlineLevel();

    public boolean getCollapsed();

    public XmlBoolean xgetCollapsed();

    public boolean isSetCollapsed();

    public void setCollapsed(boolean var1);

    public void xsetCollapsed(XmlBoolean var1);

    public void unsetCollapsed();
}

