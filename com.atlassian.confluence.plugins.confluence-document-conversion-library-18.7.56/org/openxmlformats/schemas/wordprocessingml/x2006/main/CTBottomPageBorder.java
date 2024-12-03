/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorder;

public interface CTBottomPageBorder
extends CTPageBorder {
    public static final DocumentFactory<CTBottomPageBorder> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbottompageborderde82type");
    public static final SchemaType type = Factory.getType();

    public String getBottomLeft();

    public STRelationshipId xgetBottomLeft();

    public boolean isSetBottomLeft();

    public void setBottomLeft(String var1);

    public void xsetBottomLeft(STRelationshipId var1);

    public void unsetBottomLeft();

    public String getBottomRight();

    public STRelationshipId xgetBottomRight();

    public boolean isSetBottomRight();

    public void setBottomRight(String var1);

    public void xsetBottomRight(STRelationshipId var1);

    public void unsetBottomRight();
}

