/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;

public interface CTXmlPr
extends XmlObject {
    public static final DocumentFactory<CTXmlPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctxmlpr2c58type");
    public static final SchemaType type = Factory.getType();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getMapId();

    public XmlUnsignedInt xgetMapId();

    public void setMapId(long var1);

    public void xsetMapId(XmlUnsignedInt var1);

    public String getXpath();

    public STXstring xgetXpath();

    public void setXpath(String var1);

    public void xsetXpath(STXstring var1);

    public String getXmlDataType();

    public STXmlDataType xgetXmlDataType();

    public void setXmlDataType(String var1);

    public void xsetXmlDataType(STXmlDataType var1);
}

