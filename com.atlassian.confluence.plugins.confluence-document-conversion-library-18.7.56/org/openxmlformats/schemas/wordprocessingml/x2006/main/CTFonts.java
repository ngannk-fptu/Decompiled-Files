/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHint;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTheme;

public interface CTFonts
extends XmlObject {
    public static final DocumentFactory<CTFonts> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfonts124etype");
    public static final SchemaType type = Factory.getType();

    public STHint.Enum getHint();

    public STHint xgetHint();

    public boolean isSetHint();

    public void setHint(STHint.Enum var1);

    public void xsetHint(STHint var1);

    public void unsetHint();

    public String getAscii();

    public STString xgetAscii();

    public boolean isSetAscii();

    public void setAscii(String var1);

    public void xsetAscii(STString var1);

    public void unsetAscii();

    public String getHAnsi();

    public STString xgetHAnsi();

    public boolean isSetHAnsi();

    public void setHAnsi(String var1);

    public void xsetHAnsi(STString var1);

    public void unsetHAnsi();

    public String getEastAsia();

    public STString xgetEastAsia();

    public boolean isSetEastAsia();

    public void setEastAsia(String var1);

    public void xsetEastAsia(STString var1);

    public void unsetEastAsia();

    public String getCs();

    public STString xgetCs();

    public boolean isSetCs();

    public void setCs(String var1);

    public void xsetCs(STString var1);

    public void unsetCs();

    public STTheme.Enum getAsciiTheme();

    public STTheme xgetAsciiTheme();

    public boolean isSetAsciiTheme();

    public void setAsciiTheme(STTheme.Enum var1);

    public void xsetAsciiTheme(STTheme var1);

    public void unsetAsciiTheme();

    public STTheme.Enum getHAnsiTheme();

    public STTheme xgetHAnsiTheme();

    public boolean isSetHAnsiTheme();

    public void setHAnsiTheme(STTheme.Enum var1);

    public void xsetHAnsiTheme(STTheme var1);

    public void unsetHAnsiTheme();

    public STTheme.Enum getEastAsiaTheme();

    public STTheme xgetEastAsiaTheme();

    public boolean isSetEastAsiaTheme();

    public void setEastAsiaTheme(STTheme.Enum var1);

    public void xsetEastAsiaTheme(STTheme var1);

    public void unsetEastAsiaTheme();

    public STTheme.Enum getCstheme();

    public STTheme xgetCstheme();

    public boolean isSetCstheme();

    public void setCstheme(STTheme.Enum var1);

    public void xsetCstheme(STTheme var1);

    public void unsetCstheme();
}

