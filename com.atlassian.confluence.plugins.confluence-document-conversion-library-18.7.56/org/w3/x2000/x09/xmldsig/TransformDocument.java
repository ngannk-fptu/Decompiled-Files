/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.w3.x2000.x09.xmldsig.TransformType;

public interface TransformDocument
extends XmlObject {
    public static final DocumentFactory<TransformDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "transforme335doctype");
    public static final SchemaType type = Factory.getType();

    public TransformType getTransform();

    public void setTransform(TransformType var1);

    public TransformType addNewTransform();
}

