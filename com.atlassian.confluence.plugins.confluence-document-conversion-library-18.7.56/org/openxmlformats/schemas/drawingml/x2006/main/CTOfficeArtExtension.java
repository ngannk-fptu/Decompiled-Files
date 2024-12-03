/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTOfficeArtExtension
extends XmlObject {
    public static final DocumentFactory<CTOfficeArtExtension> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctofficeartextension8e53type");
    public static final SchemaType type = Factory.getType();

    public String getUri();

    public XmlToken xgetUri();

    public void setUri(String var1);

    public void xsetUri(XmlToken var1);
}

