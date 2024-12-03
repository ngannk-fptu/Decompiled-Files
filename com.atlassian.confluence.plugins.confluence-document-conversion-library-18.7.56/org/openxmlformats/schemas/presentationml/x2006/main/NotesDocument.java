/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;

public interface NotesDocument
extends XmlObject {
    public static final DocumentFactory<NotesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "notes4a02doctype");
    public static final SchemaType type = Factory.getType();

    public CTNotesSlide getNotes();

    public void setNotes(CTNotesSlide var1);

    public CTNotesSlide addNewNotes();
}

