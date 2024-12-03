/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTRelationshipReference
extends XmlString {
    public static final DocumentFactory<CTRelationshipReference> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrelationshipreferencee68ftype");
    public static final SchemaType type = Factory.getType();

    public String getSourceId();

    public XmlString xgetSourceId();

    public void setSourceId(String var1);

    public void xsetSourceId(XmlString var1);
}

