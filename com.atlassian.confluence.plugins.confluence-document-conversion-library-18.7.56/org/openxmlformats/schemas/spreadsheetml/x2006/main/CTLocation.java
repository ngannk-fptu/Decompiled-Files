/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;

public interface CTLocation
extends XmlObject {
    public static final DocumentFactory<CTLocation> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlocationc23etype");
    public static final SchemaType type = Factory.getType();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public long getFirstHeaderRow();

    public XmlUnsignedInt xgetFirstHeaderRow();

    public void setFirstHeaderRow(long var1);

    public void xsetFirstHeaderRow(XmlUnsignedInt var1);

    public long getFirstDataRow();

    public XmlUnsignedInt xgetFirstDataRow();

    public void setFirstDataRow(long var1);

    public void xsetFirstDataRow(XmlUnsignedInt var1);

    public long getFirstDataCol();

    public XmlUnsignedInt xgetFirstDataCol();

    public void setFirstDataCol(long var1);

    public void xsetFirstDataCol(XmlUnsignedInt var1);

    public long getRowPageCount();

    public XmlUnsignedInt xgetRowPageCount();

    public boolean isSetRowPageCount();

    public void setRowPageCount(long var1);

    public void xsetRowPageCount(XmlUnsignedInt var1);

    public void unsetRowPageCount();

    public long getColPageCount();

    public XmlUnsignedInt xgetColPageCount();

    public boolean isSetColPageCount();

    public void setColPageCount(long var1);

    public void xsetColPageCount(XmlUnsignedInt var1);

    public void unsetColPageCount();
}

