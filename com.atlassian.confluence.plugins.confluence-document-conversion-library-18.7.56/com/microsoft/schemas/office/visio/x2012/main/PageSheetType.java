/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.SheetType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface PageSheetType
extends SheetType {
    public static final DocumentFactory<PageSheetType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pagesheettype679btype");
    public static final SchemaType type = Factory.getType();

    public String getUniqueID();

    public XmlString xgetUniqueID();

    public boolean isSetUniqueID();

    public void setUniqueID(String var1);

    public void xsetUniqueID(XmlString var1);

    public void unsetUniqueID();
}

