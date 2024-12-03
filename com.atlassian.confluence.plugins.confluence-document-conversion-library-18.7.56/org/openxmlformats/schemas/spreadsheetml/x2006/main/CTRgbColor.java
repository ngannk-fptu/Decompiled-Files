/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedIntHex;

public interface CTRgbColor
extends XmlObject {
    public static final DocumentFactory<CTRgbColor> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrgbcolor95dftype");
    public static final SchemaType type = Factory.getType();

    public byte[] getRgb();

    public STUnsignedIntHex xgetRgb();

    public boolean isSetRgb();

    public void setRgb(byte[] var1);

    public void xsetRgb(STUnsignedIntHex var1);

    public void unsetRgb();
}

