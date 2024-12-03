/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.PageType;
import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface PagesType
extends XmlObject {
    public static final DocumentFactory<PagesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "pagestypef2e7type");
    public static final SchemaType type = Factory.getType();

    public List<PageType> getPageList();

    public PageType[] getPageArray();

    public PageType getPageArray(int var1);

    public int sizeOfPageArray();

    public void setPageArray(PageType[] var1);

    public void setPageArray(int var1, PageType var2);

    public PageType insertNewPage(int var1);

    public PageType addNewPage();

    public void removePage(int var1);
}

