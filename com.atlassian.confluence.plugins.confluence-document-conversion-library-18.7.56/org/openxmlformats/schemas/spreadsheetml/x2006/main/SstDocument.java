/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSst;

public interface SstDocument
extends XmlObject {
    public static final DocumentFactory<SstDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "sstf81fdoctype");
    public static final SchemaType type = Factory.getType();

    public CTSst getSst();

    public void setSst(CTSst var1);

    public CTSst addNewSst();
}

