/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STLang;

public interface CTLanguage
extends XmlObject {
    public static final DocumentFactory<CTLanguage> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlanguage7b90type");
    public static final SchemaType type = Factory.getType();

    public String getVal();

    public STLang xgetVal();

    public boolean isSetVal();

    public void setVal(String var1);

    public void xsetVal(STLang var1);

    public void unsetVal();

    public String getEastAsia();

    public STLang xgetEastAsia();

    public boolean isSetEastAsia();

    public void setEastAsia(String var1);

    public void xsetEastAsia(STLang var1);

    public void unsetEastAsia();

    public String getBidi();

    public STLang xgetBidi();

    public boolean isSetBidi();

    public void setBidi(String var1);

    public void xsetBidi(STLang var1);

    public void unsetBidi();
}

