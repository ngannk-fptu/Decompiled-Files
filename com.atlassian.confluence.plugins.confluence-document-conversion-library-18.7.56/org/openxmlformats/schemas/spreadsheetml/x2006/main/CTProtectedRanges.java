/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTProtectedRange;

public interface CTProtectedRanges
extends XmlObject {
    public static final DocumentFactory<CTProtectedRanges> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctprotectedranges7e83type");
    public static final SchemaType type = Factory.getType();

    public List<CTProtectedRange> getProtectedRangeList();

    public CTProtectedRange[] getProtectedRangeArray();

    public CTProtectedRange getProtectedRangeArray(int var1);

    public int sizeOfProtectedRangeArray();

    public void setProtectedRangeArray(CTProtectedRange[] var1);

    public void setProtectedRangeArray(int var1, CTProtectedRange var2);

    public CTProtectedRange insertNewProtectedRange(int var1);

    public CTProtectedRange addNewProtectedRange();

    public void removeProtectedRange(int var1);
}

