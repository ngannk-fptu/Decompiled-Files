/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface VisioDocumentDocument1
extends XmlObject {
    public static final DocumentFactory<VisioDocumentDocument1> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "visiodocumentd431doctype");
    public static final SchemaType type = Factory.getType();

    public VisioDocumentType getVisioDocument();

    public void setVisioDocument(VisioDocumentType var1);

    public VisioDocumentType addNewVisioDocument();
}

