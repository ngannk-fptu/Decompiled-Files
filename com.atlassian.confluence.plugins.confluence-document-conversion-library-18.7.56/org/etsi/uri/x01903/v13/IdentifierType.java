/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.QualifierType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.QualifierType;

public interface IdentifierType
extends XmlAnyURI {
    public static final DocumentFactory<IdentifierType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "identifiertype2cb7type");
    public static final SchemaType type = Factory.getType();

    public QualifierType.Enum getQualifier();

    public QualifierType xgetQualifier();

    public boolean isSetQualifier();

    public void setQualifier(QualifierType.Enum var1);

    public void xsetQualifier(QualifierType var1);

    public void unsetQualifier();
}

