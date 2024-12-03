/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;

public interface CTColor
extends XmlObject {
    public static final DocumentFactory<CTColor> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolor6d4ftype");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STHexColor xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STHexColor var1);

    public STThemeColor.Enum getThemeColor();

    public STThemeColor xgetThemeColor();

    public boolean isSetThemeColor();

    public void setThemeColor(STThemeColor.Enum var1);

    public void xsetThemeColor(STThemeColor var1);

    public void unsetThemeColor();

    public byte[] getThemeTint();

    public STUcharHexNumber xgetThemeTint();

    public boolean isSetThemeTint();

    public void setThemeTint(byte[] var1);

    public void xsetThemeTint(STUcharHexNumber var1);

    public void unsetThemeTint();

    public byte[] getThemeShade();

    public STUcharHexNumber xgetThemeShade();

    public boolean isSetThemeShade();

    public void setThemeShade(byte[] var1);

    public void xsetThemeShade(STUcharHexNumber var1);

    public void unsetThemeShade();
}

