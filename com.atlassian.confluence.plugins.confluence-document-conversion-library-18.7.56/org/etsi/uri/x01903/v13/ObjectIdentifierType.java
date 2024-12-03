/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.DocumentationReferencesType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.DocumentationReferencesType;
import org.etsi.uri.x01903.v13.IdentifierType;

public interface ObjectIdentifierType
extends XmlObject {
    public static final DocumentFactory<ObjectIdentifierType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "objectidentifiertype2f56type");
    public static final SchemaType type = Factory.getType();

    public IdentifierType getIdentifier();

    public void setIdentifier(IdentifierType var1);

    public IdentifierType addNewIdentifier();

    public String getDescription();

    public XmlString xgetDescription();

    public boolean isSetDescription();

    public void setDescription(String var1);

    public void xsetDescription(XmlString var1);

    public void unsetDescription();

    public DocumentationReferencesType getDocumentationReferences();

    public boolean isSetDocumentationReferences();

    public void setDocumentationReferences(DocumentationReferencesType var1);

    public DocumentationReferencesType addNewDocumentationReferences();

    public void unsetDocumentationReferences();
}

