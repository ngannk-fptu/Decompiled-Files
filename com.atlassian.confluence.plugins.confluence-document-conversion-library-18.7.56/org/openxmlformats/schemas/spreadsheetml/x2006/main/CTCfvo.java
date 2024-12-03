/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType;

public interface CTCfvo
extends XmlObject {
    public static final DocumentFactory<CTCfvo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcfvo7ca5type");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public STCfvoType.Enum getType();

    public STCfvoType xgetType();

    public void setType(STCfvoType.Enum var1);

    public void xsetType(STCfvoType var1);

    public String getVal();

    public STXstring xgetVal();

    public boolean isSetVal();

    public void setVal(String var1);

    public void xsetVal(STXstring var1);

    public void unsetVal();

    public boolean getGte();

    public XmlBoolean xgetGte();

    public boolean isSetGte();

    public void setGte(boolean var1);

    public void xsetGte(XmlBoolean var1);

    public void unsetGte();
}

