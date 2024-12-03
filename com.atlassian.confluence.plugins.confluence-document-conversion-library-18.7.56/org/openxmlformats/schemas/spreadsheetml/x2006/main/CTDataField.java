/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STShowDataAs
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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STShowDataAs;

public interface CTDataField
extends XmlObject {
    public static final DocumentFactory<CTDataField> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdatafield6f0ftype");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getName();

    public STXstring xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public void unsetName();

    public long getFld();

    public XmlUnsignedInt xgetFld();

    public void setFld(long var1);

    public void xsetFld(XmlUnsignedInt var1);

    public STDataConsolidateFunction.Enum getSubtotal();

    public STDataConsolidateFunction xgetSubtotal();

    public boolean isSetSubtotal();

    public void setSubtotal(STDataConsolidateFunction.Enum var1);

    public void xsetSubtotal(STDataConsolidateFunction var1);

    public void unsetSubtotal();

    public STShowDataAs.Enum getShowDataAs();

    public STShowDataAs xgetShowDataAs();

    public boolean isSetShowDataAs();

    public void setShowDataAs(STShowDataAs.Enum var1);

    public void xsetShowDataAs(STShowDataAs var1);

    public void unsetShowDataAs();

    public int getBaseField();

    public XmlInt xgetBaseField();

    public boolean isSetBaseField();

    public void setBaseField(int var1);

    public void xsetBaseField(XmlInt var1);

    public void unsetBaseField();

    public long getBaseItem();

    public XmlUnsignedInt xgetBaseItem();

    public boolean isSetBaseItem();

    public void setBaseItem(long var1);

    public void xsetBaseItem(XmlUnsignedInt var1);

    public void unsetBaseItem();

    public long getNumFmtId();

    public STNumFmtId xgetNumFmtId();

    public boolean isSetNumFmtId();

    public void setNumFmtId(long var1);

    public void xsetNumFmtId(STNumFmtId var1);

    public void unsetNumFmtId();
}

