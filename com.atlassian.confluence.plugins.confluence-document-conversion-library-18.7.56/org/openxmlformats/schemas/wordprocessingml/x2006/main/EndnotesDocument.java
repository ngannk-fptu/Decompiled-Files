/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEndnotes;

public interface EndnotesDocument
extends XmlObject {
    public static final DocumentFactory<EndnotesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "endnotes960edoctype");
    public static final SchemaType type = Factory.getType();

    public CTEndnotes getEndnotes();

    public void setEndnotes(CTEndnotes var1);

    public CTEndnotes addNewEndnotes();
}

