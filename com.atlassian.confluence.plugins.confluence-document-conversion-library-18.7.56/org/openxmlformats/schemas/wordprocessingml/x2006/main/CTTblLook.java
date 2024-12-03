/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShortHexNumber;

public interface CTTblLook
extends XmlObject {
    public static final DocumentFactory<CTTblLook> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttbllooka235type");
    public static final SchemaType type = Factory.getType();

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

    public Object getNoHBand();

    public STOnOff xgetNoHBand();

    public boolean isSetNoHBand();

    public void setNoHBand(Object var1);

    public void xsetNoHBand(STOnOff var1);

    public void unsetNoHBand();

    public Object getNoVBand();

    public STOnOff xgetNoVBand();

    public boolean isSetNoVBand();

    public void setNoVBand(Object var1);

    public void xsetNoVBand(STOnOff var1);

    public void unsetNoVBand();

    public byte[] getVal();

    public STShortHexNumber xgetVal();

    public boolean isSetVal();

    public void setVal(byte[] var1);

    public void xsetVal(STShortHexNumber var1);

    public void unsetVal();
}

