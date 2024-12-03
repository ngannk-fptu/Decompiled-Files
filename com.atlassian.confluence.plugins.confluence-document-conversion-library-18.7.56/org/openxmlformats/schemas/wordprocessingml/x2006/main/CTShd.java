/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;

public interface CTShd
extends XmlObject {
    public static final DocumentFactory<CTShd> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshd58c3type");
    public static final SchemaType type = Factory.getType();

    public STShd.Enum getVal();

    public STShd xgetVal();

    public void setVal(STShd.Enum var1);

    public void xsetVal(STShd var1);

    public Object getColor();

    public STHexColor xgetColor();

    public boolean isSetColor();

    public void setColor(Object var1);

    public void xsetColor(STHexColor var1);

    public void unsetColor();

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

    public Object getFill();

    public STHexColor xgetFill();

    public boolean isSetFill();

    public void setFill(Object var1);

    public void xsetFill(STHexColor var1);

    public void unsetFill();

    public STThemeColor.Enum getThemeFill();

    public STThemeColor xgetThemeFill();

    public boolean isSetThemeFill();

    public void setThemeFill(STThemeColor.Enum var1);

    public void xsetThemeFill(STThemeColor var1);

    public void unsetThemeFill();

    public byte[] getThemeFillTint();

    public STUcharHexNumber xgetThemeFillTint();

    public boolean isSetThemeFillTint();

    public void setThemeFillTint(byte[] var1);

    public void xsetThemeFillTint(STUcharHexNumber var1);

    public void unsetThemeFillTint();

    public byte[] getThemeFillShade();

    public STUcharHexNumber xgetThemeFillShade();

    public boolean isSetThemeFillShade();

    public void setThemeFillShade(byte[] var1);

    public void xsetThemeFillShade(STUcharHexNumber var1);

    public void unsetThemeFillShade();
}

