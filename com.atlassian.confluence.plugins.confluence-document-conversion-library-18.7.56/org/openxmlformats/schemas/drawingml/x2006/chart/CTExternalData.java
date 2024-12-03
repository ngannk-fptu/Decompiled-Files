/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;

public interface CTExternalData
extends XmlObject {
    public static final DocumentFactory<CTExternalData> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctexternaldata2e07type");
    public static final SchemaType type = Factory.getType();

    public CTBoolean getAutoUpdate();

    public boolean isSetAutoUpdate();

    public void setAutoUpdate(CTBoolean var1);

    public CTBoolean addNewAutoUpdate();

    public void unsetAutoUpdate();

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);
}

