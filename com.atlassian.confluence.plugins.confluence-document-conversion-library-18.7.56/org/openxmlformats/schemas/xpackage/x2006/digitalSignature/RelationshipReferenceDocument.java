/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTRelationshipReference;

public interface RelationshipReferenceDocument
extends XmlObject {
    public static final DocumentFactory<RelationshipReferenceDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "relationshipreference8903doctype");
    public static final SchemaType type = Factory.getType();

    public CTRelationshipReference getRelationshipReference();

    public void setRelationshipReference(CTRelationshipReference var1);

    public CTRelationshipReference addNewRelationshipReference();
}

