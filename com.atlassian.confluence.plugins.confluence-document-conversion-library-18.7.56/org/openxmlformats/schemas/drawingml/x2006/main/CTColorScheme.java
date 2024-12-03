/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;

public interface CTColorScheme
extends XmlObject {
    public static final DocumentFactory<CTColorScheme> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolorscheme0e99type");
    public static final SchemaType type = Factory.getType();

    public CTColor getDk1();

    public void setDk1(CTColor var1);

    public CTColor addNewDk1();

    public CTColor getLt1();

    public void setLt1(CTColor var1);

    public CTColor addNewLt1();

    public CTColor getDk2();

    public void setDk2(CTColor var1);

    public CTColor addNewDk2();

    public CTColor getLt2();

    public void setLt2(CTColor var1);

    public CTColor addNewLt2();

    public CTColor getAccent1();

    public void setAccent1(CTColor var1);

    public CTColor addNewAccent1();

    public CTColor getAccent2();

    public void setAccent2(CTColor var1);

    public CTColor addNewAccent2();

    public CTColor getAccent3();

    public void setAccent3(CTColor var1);

    public CTColor addNewAccent3();

    public CTColor getAccent4();

    public void setAccent4(CTColor var1);

    public CTColor addNewAccent4();

    public CTColor getAccent5();

    public void setAccent5(CTColor var1);

    public CTColor addNewAccent5();

    public CTColor getAccent6();

    public void setAccent6(CTColor var1);

    public CTColor addNewAccent6();

    public CTColor getHlink();

    public void setHlink(CTColor var1);

    public CTColor addNewHlink();

    public CTColor getFolHlink();

    public void setFolHlink(CTColor var1);

    public CTColor addNewFolHlink();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public XmlString xgetName();

    public void setName(String var1);

    public void xsetName(XmlString var1);
}

