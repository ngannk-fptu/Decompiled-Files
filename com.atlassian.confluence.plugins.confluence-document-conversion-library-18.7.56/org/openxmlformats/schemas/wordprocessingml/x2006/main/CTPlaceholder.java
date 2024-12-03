/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;

public interface CTPlaceholder
extends XmlObject {
    public static final DocumentFactory<CTPlaceholder> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctplaceholder117ftype");
    public static final SchemaType type = Factory.getType();

    public CTString getDocPart();

    public void setDocPart(CTString var1);

    public CTString addNewDocPart();
}

