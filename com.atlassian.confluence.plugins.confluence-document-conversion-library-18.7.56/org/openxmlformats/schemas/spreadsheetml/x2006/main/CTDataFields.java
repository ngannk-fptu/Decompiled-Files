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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;

public interface CTDataFields
extends XmlObject {
    public static final DocumentFactory<CTDataFields> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdatafields52cctype");
    public static final SchemaType type = Factory.getType();

    public List<CTDataField> getDataFieldList();

    public CTDataField[] getDataFieldArray();

    public CTDataField getDataFieldArray(int var1);

    public int sizeOfDataFieldArray();

    public void setDataFieldArray(CTDataField[] var1);

    public void setDataFieldArray(int var1, CTDataField var2);

    public CTDataField insertNewDataField(int var1);

    public CTDataField addNewDataField();

    public void removeDataField(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}

