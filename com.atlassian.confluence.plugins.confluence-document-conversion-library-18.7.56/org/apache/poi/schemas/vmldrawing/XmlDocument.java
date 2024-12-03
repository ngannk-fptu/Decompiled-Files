/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.schemas.vmldrawing;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.poi.schemas.vmldrawing.CTXML;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface XmlDocument
extends XmlObject {
    public static final DocumentFactory<XmlDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "xml2eb5doctype");
    public static final SchemaType type = Factory.getType();

    public CTXML getXml();

    public void setXml(CTXML var1);

    public CTXML addNewXml();
}

