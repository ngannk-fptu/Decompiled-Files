/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEighthPointMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPointMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUcharHexNumber;

public interface CTBorder
extends XmlObject {
    public static final DocumentFactory<CTBorder> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbordercdfctype");
    public static final SchemaType type = Factory.getType();

    public STBorder.Enum getVal();

    public STBorder xgetVal();

    public void setVal(STBorder.Enum var1);

    public void xsetVal(STBorder var1);

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

    public BigInteger getSz();

    public STEighthPointMeasure xgetSz();

    public boolean isSetSz();

    public void setSz(BigInteger var1);

    public void xsetSz(STEighthPointMeasure var1);

    public void unsetSz();

    public BigInteger getSpace();

    public STPointMeasure xgetSpace();

    public boolean isSetSpace();

    public void setSpace(BigInteger var1);

    public void xsetSpace(STPointMeasure var1);

    public void unsetSpace();

    public Object getShadow();

    public STOnOff xgetShadow();

    public boolean isSetShadow();

    public void setShadow(Object var1);

    public void xsetShadow(STOnOff var1);

    public void unsetShadow();

    public Object getFrame();

    public STOnOff xgetFrame();

    public boolean isSetFrame();

    public void setFrame(Object var1);

    public void xsetFrame(STOnOff var1);

    public void unsetFrame();
}

