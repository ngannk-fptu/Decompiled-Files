/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;

public interface CTSheetBackgroundPicture
extends XmlObject {
    public static final DocumentFactory<CTSheetBackgroundPicture> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetbackgroundpictureaf1atype");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);
}

