/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorder;

public interface CTTopPageBorder
extends CTPageBorder {
    public static final DocumentFactory<CTTopPageBorder> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttoppageborder3c02type");
    public static final SchemaType type = Factory.getType();

    public String getTopLeft();

    public STRelationshipId xgetTopLeft();

    public boolean isSetTopLeft();

    public void setTopLeft(String var1);

    public void xsetTopLeft(STRelationshipId var1);

    public void unsetTopLeft();

    public String getTopRight();

    public STRelationshipId xgetTopRight();

    public boolean isSetTopRight();

    public void setTopRight(String var1);

    public void xsetTopRight(STRelationshipId var1);

    public void unsetTopRight();
}

