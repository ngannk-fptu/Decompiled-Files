/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;

public interface CTGraphicalObject
extends XmlObject {
    public static final DocumentFactory<CTGraphicalObject> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgraphicalobject1ce3type");
    public static final SchemaType type = Factory.getType();

    public CTGraphicalObjectData getGraphicData();

    public void setGraphicData(CTGraphicalObjectData var1);

    public CTGraphicalObjectData addNewGraphicData();
}

