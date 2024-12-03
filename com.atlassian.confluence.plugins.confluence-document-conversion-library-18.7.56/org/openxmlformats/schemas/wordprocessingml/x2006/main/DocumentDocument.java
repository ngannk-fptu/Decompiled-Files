/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;

public interface DocumentDocument
extends XmlObject {
    public static final DocumentFactory<DocumentDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "document2bd9doctype");
    public static final SchemaType type = Factory.getType();

    public CTDocument1 getDocument();

    public void setDocument(CTDocument1 var1);

    public CTDocument1 addNewDocument();
}

