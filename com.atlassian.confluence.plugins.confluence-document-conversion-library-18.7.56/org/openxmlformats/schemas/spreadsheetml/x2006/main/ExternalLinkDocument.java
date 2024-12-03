/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalLink;

public interface ExternalLinkDocument
extends XmlObject {
    public static final DocumentFactory<ExternalLinkDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "externallinkb4c2doctype");
    public static final SchemaType type = Factory.getType();

    public CTExternalLink getExternalLink();

    public void setExternalLink(CTExternalLink var1);

    public CTExternalLink addNewExternalLink();
}

