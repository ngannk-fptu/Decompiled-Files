/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;

public interface CTPageField
extends XmlObject {
    public static final DocumentFactory<CTPageField> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagefield338atype");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public int getFld();

    public XmlInt xgetFld();

    public void setFld(int var1);

    public void xsetFld(XmlInt var1);

    public long getItem();

    public XmlUnsignedInt xgetItem();

    public boolean isSetItem();

    public void setItem(long var1);

    public void xsetItem(XmlUnsignedInt var1);

    public void unsetItem();

    public int getHier();

    public XmlInt xgetHier();

    public boolean isSetHier();

    public void setHier(int var1);

    public void xsetHier(XmlInt var1);

    public void unsetHier();

    public String getName();

    public STXstring xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public void unsetName();

    public String getCap();

    public STXstring xgetCap();

    public boolean isSetCap();

    public void setCap(String var1);

    public void xsetCap(STXstring var1);

    public void unsetCap();
}

