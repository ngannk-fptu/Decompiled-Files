/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMapInfo;

public interface MapInfoDocument
extends XmlObject {
    public static final DocumentFactory<MapInfoDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "mapinfo5715doctype");
    public static final SchemaType type = Factory.getType();

    public CTMapInfo getMapInfo();

    public void setMapInfo(CTMapInfo var1);

    public CTMapInfo addNewMapInfo();
}

