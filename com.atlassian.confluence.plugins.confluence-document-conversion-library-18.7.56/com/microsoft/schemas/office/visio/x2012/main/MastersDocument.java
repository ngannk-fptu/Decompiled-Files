/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main;

import com.microsoft.schemas.office.visio.x2012.main.MastersType;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface MastersDocument
extends XmlObject {
    public static final DocumentFactory<MastersDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "masters0341doctype");
    public static final SchemaType type = Factory.getType();

    public MastersType getMasters();

    public void setMasters(MastersType var1);

    public MastersType addNewMasters();
}

