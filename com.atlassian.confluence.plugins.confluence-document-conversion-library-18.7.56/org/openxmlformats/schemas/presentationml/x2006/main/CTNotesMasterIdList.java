/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdListEntry;

public interface CTNotesMasterIdList
extends XmlObject {
    public static final DocumentFactory<CTNotesMasterIdList> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnotesmasteridlist2853type");
    public static final SchemaType type = Factory.getType();

    public CTNotesMasterIdListEntry getNotesMasterId();

    public boolean isSetNotesMasterId();

    public void setNotesMasterId(CTNotesMasterIdListEntry var1);

    public CTNotesMasterIdListEntry addNewNotesMasterId();

    public void unsetNotesMasterId();
}

