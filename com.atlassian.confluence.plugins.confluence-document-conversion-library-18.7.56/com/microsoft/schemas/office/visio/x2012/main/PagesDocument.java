/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.PagesType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface PagesDocument
extends XmlObject {
    public static final DocumentFactory<PagesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pages52f4doctype");
    public static final SchemaType type = Factory.getType();

    public PagesType getPages();

    public void setPages(PagesType var1);

    public PagesType addNewPages();
}

