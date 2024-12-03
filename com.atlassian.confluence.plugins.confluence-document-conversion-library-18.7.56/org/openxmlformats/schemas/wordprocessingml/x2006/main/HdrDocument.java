/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;

public interface HdrDocument
extends XmlObject {
    public static final DocumentFactory<HdrDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "hdra530doctype");
    public static final SchemaType type = Factory.getType();

    public CTHdrFtr getHdr();

    public void setHdr(CTHdrFtr var1);

    public CTHdrFtr addNewHdr();
}

