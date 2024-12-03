/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;

public interface CTMapInfo
extends XmlObject {
    public static final DocumentFactory<CTMapInfo> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmapinfo1a09type");
    public static final SchemaType type = Factory.getType();

    public List<CTSchema> getSchemaList();

    public CTSchema[] getSchemaArray();

    public CTSchema getSchemaArray(int var1);

    public int sizeOfSchemaArray();

    public void setSchemaArray(CTSchema[] var1);

    public void setSchemaArray(int var1, CTSchema var2);

    public CTSchema insertNewSchema(int var1);

    public CTSchema addNewSchema();

    public void removeSchema(int var1);

    public List<CTMap> getMapList();

    public CTMap[] getMapArray();

    public CTMap getMapArray(int var1);

    public int sizeOfMapArray();

    public void setMapArray(CTMap[] var1);

    public void setMapArray(int var1, CTMap var2);

    public CTMap insertNewMap(int var1);

    public CTMap addNewMap();

    public void removeMap(int var1);

    public String getSelectionNamespaces();

    public XmlString xgetSelectionNamespaces();

    public void setSelectionNamespaces(String var1);

    public void xsetSelectionNamespaces(XmlString var1);
}

