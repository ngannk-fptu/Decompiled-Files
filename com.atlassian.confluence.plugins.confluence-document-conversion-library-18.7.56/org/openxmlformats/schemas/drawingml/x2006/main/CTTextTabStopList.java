/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;

public interface CTTextTabStopList
extends XmlObject {
    public static final DocumentFactory<CTTextTabStopList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttexttabstoplistf539type");
    public static final SchemaType type = Factory.getType();

    public List<CTTextTabStop> getTabList();

    public CTTextTabStop[] getTabArray();

    public CTTextTabStop getTabArray(int var1);

    public int sizeOfTabArray();

    public void setTabArray(CTTextTabStop[] var1);

    public void setTabArray(int var1, CTTextTabStop var2);

    public CTTextTabStop insertNewTab(int var1);

    public CTTextTabStop addNewTab();

    public void removeTab(int var1);
}

