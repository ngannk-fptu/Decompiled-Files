/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTCellFormula
extends STFormula {
    public static final DocumentFactory<CTCellFormula> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcellformula3583type");
    public static final SchemaType type = Factory.getType();

    public STCellFormulaType.Enum getT();

    public STCellFormulaType xgetT();

    public boolean isSetT();

    public void setT(STCellFormulaType.Enum var1);

    public void xsetT(STCellFormulaType var1);

    public void unsetT();

    public boolean getAca();

    public XmlBoolean xgetAca();

    public boolean isSetAca();

    public void setAca(boolean var1);

    public void xsetAca(XmlBoolean var1);

    public void unsetAca();

    public String getRef();

    public STRef xgetRef();

    public boolean isSetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public void unsetRef();

    public boolean getDt2D();

    public XmlBoolean xgetDt2D();

    public boolean isSetDt2D();

    public void setDt2D(boolean var1);

    public void xsetDt2D(XmlBoolean var1);

    public void unsetDt2D();

    public boolean getDtr();

    public XmlBoolean xgetDtr();

    public boolean isSetDtr();

    public void setDtr(boolean var1);

    public void xsetDtr(XmlBoolean var1);

    public void unsetDtr();

    public boolean getDel1();

    public XmlBoolean xgetDel1();

    public boolean isSetDel1();

    public void setDel1(boolean var1);

    public void xsetDel1(XmlBoolean var1);

    public void unsetDel1();

    public boolean getDel2();

    public XmlBoolean xgetDel2();

    public boolean isSetDel2();

    public void setDel2(boolean var1);

    public void xsetDel2(XmlBoolean var1);

    public void unsetDel2();

    public String getR1();

    public STCellRef xgetR1();

    public boolean isSetR1();

    public void setR1(String var1);

    public void xsetR1(STCellRef var1);

    public void unsetR1();

    public String getR2();

    public STCellRef xgetR2();

    public boolean isSetR2();

    public void setR2(String var1);

    public void xsetR2(STCellRef var1);

    public void unsetR2();

    public boolean getCa();

    public XmlBoolean xgetCa();

    public boolean isSetCa();

    public void setCa(boolean var1);

    public void xsetCa(XmlBoolean var1);

    public void unsetCa();

    public long getSi();

    public XmlUnsignedInt xgetSi();

    public boolean isSetSi();

    public void setSi(long var1);

    public void xsetSi(XmlUnsignedInt var1);

    public void unsetSi();

    public boolean getBx();

    public XmlBoolean xgetBx();

    public boolean isSetBx();

    public void setBx(boolean var1);

    public void xsetBx(XmlBoolean var1);

    public void unsetBx();
}

