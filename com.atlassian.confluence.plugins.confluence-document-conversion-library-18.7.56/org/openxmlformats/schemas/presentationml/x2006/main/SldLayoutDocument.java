/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayout;

public interface SldLayoutDocument
extends XmlObject {
    public static final DocumentFactory<SldLayoutDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "sldlayout638edoctype");
    public static final SchemaType type = Factory.getType();

    public CTSlideLayout getSldLayout();

    public void setSldLayout(CTSlideLayout var1);

    public CTSlideLayout addNewSldLayout();
}

