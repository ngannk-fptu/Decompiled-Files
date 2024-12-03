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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STItemType;

public interface CTItem
extends XmlObject {
    public static final DocumentFactory<CTItem> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctitemc69ctype");
    public static final SchemaType type = Factory.getType();

    public String getN();

    public STXstring xgetN();

    public boolean isSetN();

    public void setN(String var1);

    public void xsetN(STXstring var1);

    public void unsetN();

    public STItemType.Enum getT();

    public STItemType xgetT();

    public boolean isSetT();

    public void setT(STItemType.Enum var1);

    public void xsetT(STItemType var1);

    public void unsetT();

    public boolean getH();

    public XmlBoolean xgetH();

    public boolean isSetH();

    public void setH(boolean var1);

    public void xsetH(XmlBoolean var1);

    public void unsetH();

    public boolean getS();

    public XmlBoolean xgetS();

    public boolean isSetS();

    public void setS(boolean var1);

    public void xsetS(XmlBoolean var1);

    public void unsetS();

    public boolean getSd();

    public XmlBoolean xgetSd();

    public boolean isSetSd();

    public void setSd(boolean var1);

    public void xsetSd(XmlBoolean var1);

    public void unsetSd();

    public boolean getF();

    public XmlBoolean xgetF();

    public boolean isSetF();

    public void setF(boolean var1);

    public void xsetF(XmlBoolean var1);

    public void unsetF();

    public boolean getM();

    public XmlBoolean xgetM();

    public boolean isSetM();

    public void setM(boolean var1);

    public void xsetM(XmlBoolean var1);

    public void unsetM();

    public boolean getC();

    public XmlBoolean xgetC();

    public boolean isSetC();

    public void setC(boolean var1);

    public void xsetC(XmlBoolean var1);

    public void unsetC();

    public long getX();

    public XmlUnsignedInt xgetX();

    public boolean isSetX();

    public void setX(long var1);

    public void xsetX(XmlUnsignedInt var1);

    public void unsetX();

    public boolean getD();

    public XmlBoolean xgetD();

    public boolean isSetD();

    public void setD(boolean var1);

    public void xsetD(XmlBoolean var1);

    public void unsetD();

    public boolean getE();

    public XmlBoolean xgetE();

    public boolean isSetE();

    public void setE(boolean var1);

    public void xsetE(XmlBoolean var1);

    public void unsetE();
}

