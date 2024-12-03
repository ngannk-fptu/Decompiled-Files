/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;

public interface CTPivotFields
extends XmlObject {
    public static final DocumentFactory<CTPivotFields> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpivotfields12batype");
    public static final SchemaType type = Factory.getType();

    public List<CTPivotField> getPivotFieldList();

    public CTPivotField[] getPivotFieldArray();

    public CTPivotField getPivotFieldArray(int var1);

    public int sizeOfPivotFieldArray();

    public void setPivotFieldArray(CTPivotField[] var1);

    public void setPivotFieldArray(int var1, CTPivotField var2);

    public CTPivotField insertNewPivotField(int var1);

    public CTPivotField addNewPivotField();

    public void removePivotField(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}

