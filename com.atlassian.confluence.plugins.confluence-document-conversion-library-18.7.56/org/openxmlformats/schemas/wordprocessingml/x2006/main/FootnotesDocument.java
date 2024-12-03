/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;

public interface FootnotesDocument
extends XmlObject {
    public static final DocumentFactory<FootnotesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "footnotes8773doctype");
    public static final SchemaType type = Factory.getType();

    public CTFootnotes getFootnotes();

    public void setFootnotes(CTFootnotes var1);

    public CTFootnotes addNewFootnotes();
}

