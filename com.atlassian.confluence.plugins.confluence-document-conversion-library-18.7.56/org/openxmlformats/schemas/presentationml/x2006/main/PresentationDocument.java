/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;

public interface PresentationDocument
extends XmlObject {
    public static final DocumentFactory<PresentationDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "presentation02f7doctype");
    public static final SchemaType type = Factory.getType();

    public CTPresentation getPresentation();

    public void setPresentation(CTPresentation var1);

    public CTPresentation addNewPresentation();
}

