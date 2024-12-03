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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageField;

public interface CTPageFields
extends XmlObject {
    public static final DocumentFactory<CTPageFields> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpagefields1db1type");
    public static final SchemaType type = Factory.getType();

    public List<CTPageField> getPageFieldList();

    public CTPageField[] getPageFieldArray();

    public CTPageField getPageFieldArray(int var1);

    public int sizeOfPageFieldArray();

    public void setPageFieldArray(CTPageField[] var1);

    public void setPageFieldArray(int var1, CTPageField var2);

    public CTPageField insertNewPageField(int var1);

    public CTPageField addNewPageField();

    public void removePageField(int var1);

    public long getCount();

    public XmlUnsignedInt xgetCount();

    public boolean isSetCount();

    public void setCount(long var1);

    public void xsetCount(XmlUnsignedInt var1);

    public void unsetCount();
}

