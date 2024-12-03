/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShortHexNumber;

public interface CTSym
extends XmlObject {
    public static final DocumentFactory<CTSym> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsym0dabtype");
    public static final SchemaType type = Factory.getType();

    public String getFont();

    public STString xgetFont();

    public boolean isSetFont();

    public void setFont(String var1);

    public void xsetFont(STString var1);

    public void unsetFont();

    public byte[] getChar();

    public STShortHexNumber xgetChar();

    public boolean isSetChar();

    public void setChar(byte[] var1);

    public void xsetChar(STShortHexNumber var1);

    public void unsetChar();
}

