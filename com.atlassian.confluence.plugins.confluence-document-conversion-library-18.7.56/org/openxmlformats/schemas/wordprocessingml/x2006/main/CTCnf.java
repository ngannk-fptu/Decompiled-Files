/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STCnf;

public interface CTCnf
extends XmlObject {
    public static final DocumentFactory<CTCnf> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcnf1397type");
    public static final SchemaType type = Factory.getType();

    public String getVal();

    public STCnf xgetVal();

    public boolean isSetVal();

    public void setVal(String var1);

    public void xsetVal(STCnf var1);

    public void unsetVal();

    public Object getFirstRow();

    public STOnOff xgetFirstRow();

    public boolean isSetFirstRow();

    public void setFirstRow(Object var1);

    public void xsetFirstRow(STOnOff var1);

    public void unsetFirstRow();

    public Object getLastRow();

    public STOnOff xgetLastRow();

    public boolean isSetLastRow();

    public void setLastRow(Object var1);

    public void xsetLastRow(STOnOff var1);

    public void unsetLastRow();

    public Object getFirstColumn();

    public STOnOff xgetFirstColumn();

    public boolean isSetFirstColumn();

    public void setFirstColumn(Object var1);

    public void xsetFirstColumn(STOnOff var1);

    public void unsetFirstColumn();

    public Object getLastColumn();

    public STOnOff xgetLastColumn();

    public boolean isSetLastColumn();

    public void setLastColumn(Object var1);

    public void xsetLastColumn(STOnOff var1);

    public void unsetLastColumn();

    public Object getOddVBand();

    public STOnOff xgetOddVBand();

    public boolean isSetOddVBand();

    public void setOddVBand(Object var1);

    public void xsetOddVBand(STOnOff var1);

    public void unsetOddVBand();

    public Object getEvenVBand();

    public STOnOff xgetEvenVBand();

    public boolean isSetEvenVBand();

    public void setEvenVBand(Object var1);

    public void xsetEvenVBand(STOnOff var1);

    public void unsetEvenVBand();

    public Object getOddHBand();

    public STOnOff xgetOddHBand();

    public boolean isSetOddHBand();

    public void setOddHBand(Object var1);

    public void xsetOddHBand(STOnOff var1);

    public void unsetOddHBand();

    public Object getEvenHBand();

    public STOnOff xgetEvenHBand();

    public boolean isSetEvenHBand();

    public void setEvenHBand(Object var1);

    public void xsetEvenHBand(STOnOff var1);

    public void unsetEvenHBand();

    public Object getFirstRowFirstColumn();

    public STOnOff xgetFirstRowFirstColumn();

    public boolean isSetFirstRowFirstColumn();

    public void setFirstRowFirstColumn(Object var1);

    public void xsetFirstRowFirstColumn(STOnOff var1);

    public void unsetFirstRowFirstColumn();

    public Object getFirstRowLastColumn();

    public STOnOff xgetFirstRowLastColumn();

    public boolean isSetFirstRowLastColumn();

    public void setFirstRowLastColumn(Object var1);

    public void xsetFirstRowLastColumn(STOnOff var1);

    public void unsetFirstRowLastColumn();

    public Object getLastRowFirstColumn();

    public STOnOff xgetLastRowFirstColumn();

    public boolean isSetLastRowFirstColumn();

    public void setLastRowFirstColumn(Object var1);

    public void xsetLastRowFirstColumn(STOnOff var1);

    public void unsetLastRowFirstColumn();

    public Object getLastRowLastColumn();

    public STOnOff xgetLastRowLastColumn();

    public boolean isSetLastRowLastColumn();

    public void setLastRowLastColumn(Object var1);

    public void xsetLastRowLastColumn(STOnOff var1);

    public void unsetLastRowLastColumn();
}

