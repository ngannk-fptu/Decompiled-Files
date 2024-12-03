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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTField;

public interface CTRowFields
extends XmlObject {
    public static final DocumentFactory<CTRowFields> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrowfields0312type");
    public static final SchemaType type = Factory.getType();

    public List<CTField> getFieldList();

    public CTField[] getFieldArray();

    public CTField getFieldArray(int var1);

    public int sizeOfFieldArray();

    public void setFieldArray(CTField[] var1);

    public void setFieldArray(int var1, CTField var2);

    public CTField insertNewField(int var1);

    public CTField addNewField();

    public void removeField(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}

