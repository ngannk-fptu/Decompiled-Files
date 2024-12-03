/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumn
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.math.BigInteger;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;

public interface CTColumns
extends XmlObject {
    public static final DocumentFactory<CTColumns> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcolumns51d5type");
    public static final SchemaType type = Factory.getType();

    public List<CTColumn> getColList();

    public CTColumn[] getColArray();

    public CTColumn getColArray(int var1);

    public int sizeOfColArray();

    public void setColArray(CTColumn[] var1);

    public void setColArray(int var1, CTColumn var2);

    public CTColumn insertNewCol(int var1);

    public CTColumn addNewCol();

    public void removeCol(int var1);

    public Object getEqualWidth();

    public STOnOff xgetEqualWidth();

    public boolean isSetEqualWidth();

    public void setEqualWidth(Object var1);

    public void xsetEqualWidth(STOnOff var1);

    public void unsetEqualWidth();

    public Object getSpace();

    public STTwipsMeasure xgetSpace();

    public boolean isSetSpace();

    public void setSpace(Object var1);

    public void xsetSpace(STTwipsMeasure var1);

    public void unsetSpace();

    public BigInteger getNum();

    public STDecimalNumber xgetNum();

    public boolean isSetNum();

    public void setNum(BigInteger var1);

    public void xsetNum(STDecimalNumber var1);

    public void unsetNum();

    public Object getSep();

    public STOnOff xgetSep();

    public boolean isSetSep();

    public void setSep(Object var1);

    public void xsetSep(STOnOff var1);

    public void unsetSep();
}

