/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

public interface CTCell
extends XmlObject {
    public static final DocumentFactory<CTCell> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcell842btype");
    public static final SchemaType type = Factory.getType();

    public CTCellFormula getF();

    public boolean isSetF();

    public void setF(CTCellFormula var1);

    public CTCellFormula addNewF();

    public void unsetF();

    public String getV();

    public STXstring xgetV();

    public boolean isSetV();

    public void setV(String var1);

    public void xsetV(STXstring var1);

    public void unsetV();

    public CTRst getIs();

    public boolean isSetIs();

    public void setIs(CTRst var1);

    public CTRst addNewIs();

    public void unsetIs();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getR();

    public STCellRef xgetR();

    public boolean isSetR();

    public void setR(String var1);

    public void xsetR(STCellRef var1);

    public void unsetR();

    public long getS();

    public XmlUnsignedInt xgetS();

    public boolean isSetS();

    public void setS(long var1);

    public void xsetS(XmlUnsignedInt var1);

    public void unsetS();

    public STCellType.Enum getT();

    public STCellType xgetT();

    public boolean isSetT();

    public void setT(STCellType.Enum var1);

    public void xsetT(STCellType var1);

    public void unsetT();

    public long getCm();

    public XmlUnsignedInt xgetCm();

    public boolean isSetCm();

    public void setCm(long var1);

    public void xsetCm(XmlUnsignedInt var1);

    public void unsetCm();

    public long getVm();

    public XmlUnsignedInt xgetVm();

    public boolean isSetVm();

    public void setVm(long var1);

    public void xsetVm(XmlUnsignedInt var1);

    public void unsetVm();

    public boolean getPh();

    public XmlBoolean xgetPh();

    public boolean isSetPh();

    public void setPh(boolean var1);

    public void xsetPh(XmlBoolean var1);

    public void unsetPh();
}

