/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;

public interface FtrDocument
extends XmlObject {
    public static final DocumentFactory<FtrDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ftre182doctype");
    public static final SchemaType type = Factory.getType();

    public CTHdrFtr getFtr();

    public void setFtr(CTHdrFtr var1);

    public CTHdrFtr addNewFtr();
}

