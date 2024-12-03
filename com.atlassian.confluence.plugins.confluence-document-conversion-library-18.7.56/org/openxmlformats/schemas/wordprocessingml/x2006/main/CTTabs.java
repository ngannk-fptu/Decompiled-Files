/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;

public interface CTTabs
extends XmlObject {
    public static final DocumentFactory<CTTabs> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttabsa2aatype");
    public static final SchemaType type = Factory.getType();

    public List<CTTabStop> getTabList();

    public CTTabStop[] getTabArray();

    public CTTabStop getTabArray(int var1);

    public int sizeOfTabArray();

    public void setTabArray(CTTabStop[] var1);

    public void setTabArray(int var1, CTTabStop var2);

    public CTTabStop insertNewTab(int var1);

    public CTTabStop addNewTab();

    public void removeTab(int var1);
}

