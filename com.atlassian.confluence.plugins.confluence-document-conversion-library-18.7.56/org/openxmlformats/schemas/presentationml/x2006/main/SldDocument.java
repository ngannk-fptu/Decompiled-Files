/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

public interface SldDocument
extends XmlObject {
    public static final DocumentFactory<SldDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "sld1b98doctype");
    public static final SchemaType type = Factory.getType();

    public CTSlide getSld();

    public void setSld(CTSlide var1);

    public CTSlide addNewSld();
}

