/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedIntHex;

public interface CTColor
extends XmlObject {
    public static final DocumentFactory<CTColor> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolord2c2type");
    public static final SchemaType type = Factory.getType();

    public boolean getAuto();

    public XmlBoolean xgetAuto();

    public boolean isSetAuto();

    public void setAuto(boolean var1);

    public void xsetAuto(XmlBoolean var1);

    public void unsetAuto();

    public long getIndexed();

    public XmlUnsignedInt xgetIndexed();

    public boolean isSetIndexed();

    public void setIndexed(long var1);

    public void xsetIndexed(XmlUnsignedInt var1);

    public void unsetIndexed();

    public byte[] getRgb();

    public STUnsignedIntHex xgetRgb();

    public boolean isSetRgb();

    public void setRgb(byte[] var1);

    public void xsetRgb(STUnsignedIntHex var1);

    public void unsetRgb();

    public long getTheme();

    public XmlUnsignedInt xgetTheme();

    public boolean isSetTheme();

    public void setTheme(long var1);

    public void xsetTheme(XmlUnsignedInt var1);

    public void unsetTheme();

    public double getTint();

    public XmlDouble xgetTint();

    public boolean isSetTint();

    public void setTint(double var1);

    public void xsetTint(XmlDouble var1);

    public void unsetTint();
}

