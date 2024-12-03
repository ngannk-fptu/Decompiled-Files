/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTTextCharBullet
extends XmlObject {
    public static final DocumentFactory<CTTextCharBullet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextcharbullet3c20type");
    public static final SchemaType type = Factory.getType();

    public String getChar();

    public XmlString xgetChar();

    public void setChar(String var1);

    public void xsetChar(XmlString var1);
}

