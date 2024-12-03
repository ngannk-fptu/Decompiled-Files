/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAltChunkPr
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAltChunkPr;

public interface CTAltChunk
extends XmlObject {
    public static final DocumentFactory<CTAltChunk> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctaltchunk5c24type");
    public static final SchemaType type = Factory.getType();

    public CTAltChunkPr getAltChunkPr();

    public boolean isSetAltChunkPr();

    public void setAltChunkPr(CTAltChunkPr var1);

    public CTAltChunkPr addNewAltChunkPr();

    public void unsetAltChunkPr();

    public String getId();

    public STRelationshipId xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STRelationshipId var1);

    public void unsetId();
}

