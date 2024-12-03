/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;

public interface NumberingDocument
extends XmlObject {
    public static final DocumentFactory<NumberingDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "numbering1c4ddoctype");
    public static final SchemaType type = Factory.getType();

    public CTNumbering getNumbering();

    public void setNumbering(CTNumbering var1);

    public CTNumbering addNewNumbering();
}

