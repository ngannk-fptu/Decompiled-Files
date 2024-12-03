/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.STColorSchemeIndex;

public interface CTColorMapping
extends XmlObject {
    public static final DocumentFactory<CTColorMapping> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolormapping5bc6type");
    public static final SchemaType type = Factory.getType();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public STColorSchemeIndex.Enum getBg1();

    public STColorSchemeIndex xgetBg1();

    public void setBg1(STColorSchemeIndex.Enum var1);

    public void xsetBg1(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getTx1();

    public STColorSchemeIndex xgetTx1();

    public void setTx1(STColorSchemeIndex.Enum var1);

    public void xsetTx1(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getBg2();

    public STColorSchemeIndex xgetBg2();

    public void setBg2(STColorSchemeIndex.Enum var1);

    public void xsetBg2(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getTx2();

    public STColorSchemeIndex xgetTx2();

    public void setTx2(STColorSchemeIndex.Enum var1);

    public void xsetTx2(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getAccent1();

    public STColorSchemeIndex xgetAccent1();

    public void setAccent1(STColorSchemeIndex.Enum var1);

    public void xsetAccent1(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getAccent2();

    public STColorSchemeIndex xgetAccent2();

    public void setAccent2(STColorSchemeIndex.Enum var1);

    public void xsetAccent2(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getAccent3();

    public STColorSchemeIndex xgetAccent3();

    public void setAccent3(STColorSchemeIndex.Enum var1);

    public void xsetAccent3(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getAccent4();

    public STColorSchemeIndex xgetAccent4();

    public void setAccent4(STColorSchemeIndex.Enum var1);

    public void xsetAccent4(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getAccent5();

    public STColorSchemeIndex xgetAccent5();

    public void setAccent5(STColorSchemeIndex.Enum var1);

    public void xsetAccent5(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getAccent6();

    public STColorSchemeIndex xgetAccent6();

    public void setAccent6(STColorSchemeIndex.Enum var1);

    public void xsetAccent6(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getHlink();

    public STColorSchemeIndex xgetHlink();

    public void setHlink(STColorSchemeIndex.Enum var1);

    public void xsetHlink(STColorSchemeIndex var1);

    public STColorSchemeIndex.Enum getFolHlink();

    public STColorSchemeIndex xgetFolHlink();

    public void setFolHlink(STColorSchemeIndex.Enum var1);

    public void xsetFolHlink(STColorSchemeIndex var1);
}

