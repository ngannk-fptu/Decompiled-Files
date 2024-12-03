/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMaster;

public interface NotesMasterDocument
extends XmlObject {
    public static final DocumentFactory<NotesMasterDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "notesmaster8840doctype");
    public static final SchemaType type = Factory.getType();

    public CTNotesMaster getNotesMaster();

    public void setNotesMaster(CTNotesMaster var1);

    public CTNotesMaster addNewNotesMaster();
}

