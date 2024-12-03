/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.diagram;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;

public interface CTRelIds
extends XmlObject {
    public static final DocumentFactory<CTRelIds> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrelidsfef2type");
    public static final SchemaType type = Factory.getType();

    public String getDm();

    public STRelationshipId xgetDm();

    public void setDm(String var1);

    public void xsetDm(STRelationshipId var1);

    public String getLo();

    public STRelationshipId xgetLo();

    public void setLo(String var1);

    public void xsetLo(STRelationshipId var1);

    public String getQs();

    public STRelationshipId xgetQs();

    public void setQs(String var1);

    public void xsetQs(STRelationshipId var1);

    public String getCs();

    public STRelationshipId xgetCs();

    public void setCs(String var1);

    public void xsetCs(STRelationshipId var1);
}

