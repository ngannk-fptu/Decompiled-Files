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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

public interface CTExternalCell
extends XmlObject {
    public static final DocumentFactory<CTExternalCell> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternalcell5dd6type");
    public static final SchemaType type = Factory.getType();

    public String getV();

    public STXstring xgetV();

    public boolean isSetV();

    public void setV(String var1);

    public void xsetV(STXstring var1);

    public void unsetV();

    public String getR();

    public STCellRef xgetR();

    public boolean isSetR();

    public void setR(String var1);

    public void xsetR(STCellRef var1);

    public void unsetR();

    public STCellType.Enum getT();

    public STCellType xgetT();

    public boolean isSetT();

    public void setT(STCellType.Enum var1);

    public void xsetT(STCellType var1);

    public void unsetT();

    public long getVm();

    public XmlUnsignedInt xgetVm();

    public boolean isSetVm();

    public void setVm(long var1);

    public void xsetVm(XmlUnsignedInt var1);

    public void unsetVm();
}

