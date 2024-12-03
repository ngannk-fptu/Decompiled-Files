/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;

public interface CTTableStyleList
extends XmlObject {
    public static final DocumentFactory<CTTableStyleList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablestylelist4bdctype");
    public static final SchemaType type = Factory.getType();

    public List<CTTableStyle> getTblStyleList();

    public CTTableStyle[] getTblStyleArray();

    public CTTableStyle getTblStyleArray(int var1);

    public int sizeOfTblStyleArray();

    public void setTblStyleArray(CTTableStyle[] var1);

    public void setTblStyleArray(int var1, CTTableStyle var2);

    public CTTableStyle insertNewTblStyle(int var1);

    public CTTableStyle addNewTblStyle();

    public void removeTblStyle(int var1);

    public String getDef();

    public STGuid xgetDef();

    public void setDef(String var1);

    public void xsetDef(STGuid var1);
}

