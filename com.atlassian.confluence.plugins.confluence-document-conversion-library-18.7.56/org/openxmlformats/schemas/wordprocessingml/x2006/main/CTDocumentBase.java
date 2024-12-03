/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBackground;

public interface CTDocumentBase
extends XmlObject {
    public static final DocumentFactory<CTDocumentBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdocumentbasedf5ctype");
    public static final SchemaType type = Factory.getType();

    public CTBackground getBackground();

    public boolean isSetBackground();

    public void setBackground(CTBackground var1);

    public CTBackground addNewBackground();

    public void unsetBackground();
}

