/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;

public interface CTCalcCell
extends XmlObject {
    public static final DocumentFactory<CTCalcCell> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcalccellb960type");
    public static final SchemaType type = Factory.getType();

    public String getR();

    public STCellRef xgetR();

    public boolean isSetR();

    public void setR(String var1);

    public void xsetR(STCellRef var1);

    public void unsetR();

    public String getRef();

    public STCellRef xgetRef();

    public boolean isSetRef();

    public void setRef(String var1);

    public void xsetRef(STCellRef var1);

    public void unsetRef();

    public int getI();

    public XmlInt xgetI();

    public boolean isSetI();

    public void setI(int var1);

    public void xsetI(XmlInt var1);

    public void unsetI();

    public boolean getS();

    public XmlBoolean xgetS();

    public boolean isSetS();

    public void setS(boolean var1);

    public void xsetS(XmlBoolean var1);

    public void unsetS();

    public boolean getL();

    public XmlBoolean xgetL();

    public boolean isSetL();

    public void setL(boolean var1);

    public void xsetL(XmlBoolean var1);

    public void unsetL();

    public boolean getT();

    public XmlBoolean xgetT();

    public boolean isSetT();

    public void setT(boolean var1);

    public void xsetT(XmlBoolean var1);

    public void unsetT();

    public boolean getA();

    public XmlBoolean xgetA();

    public boolean isSetA();

    public void setA(boolean var1);

    public void xsetA(XmlBoolean var1);

    public void unsetA();
}

