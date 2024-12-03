/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;

public interface CTRel
extends XmlObject {
    public static final DocumentFactory<CTRel> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrelf116type");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public STRelationshipId xgetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);
}

