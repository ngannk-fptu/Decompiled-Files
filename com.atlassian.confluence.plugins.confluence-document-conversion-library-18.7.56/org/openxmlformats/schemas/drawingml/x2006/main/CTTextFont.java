/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlByte;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPitchFamily;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTypeface;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPanose;

public interface CTTextFont
extends XmlObject {
    public static final DocumentFactory<CTTextFont> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextfont92b7type");
    public static final SchemaType type = Factory.getType();

    public String getTypeface();

    public STTextTypeface xgetTypeface();

    public void setTypeface(String var1);

    public void xsetTypeface(STTextTypeface var1);

    public byte[] getPanose();

    public STPanose xgetPanose();

    public boolean isSetPanose();

    public void setPanose(byte[] var1);

    public void xsetPanose(STPanose var1);

    public void unsetPanose();

    public byte getPitchFamily();

    public STPitchFamily xgetPitchFamily();

    public boolean isSetPitchFamily();

    public void setPitchFamily(byte var1);

    public void xsetPitchFamily(STPitchFamily var1);

    public void unsetPitchFamily();

    public byte getCharset();

    public XmlByte xgetCharset();

    public boolean isSetCharset();

    public void setCharset(byte var1);

    public void xsetCharset(XmlByte var1);

    public void unsetCharset();
}

