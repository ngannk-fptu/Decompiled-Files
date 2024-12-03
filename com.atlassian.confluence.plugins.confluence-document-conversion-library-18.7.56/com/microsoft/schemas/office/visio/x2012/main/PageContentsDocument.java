/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.PageContentsType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface PageContentsDocument
extends XmlObject {
    public static final DocumentFactory<PageContentsDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pagecontentsfc8bdoctype");
    public static final SchemaType type = Factory.getType();

    public PageContentsType getPageContents();

    public void setPageContents(PageContentsType var1);

    public PageContentsType addNewPageContents();
}

