/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;

public interface QualifyingPropertiesDocument
extends XmlObject {
    public static final DocumentFactory<QualifyingPropertiesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "qualifyingproperties53ccdoctype");
    public static final SchemaType type = Factory.getType();

    public QualifyingPropertiesType getQualifyingProperties();

    public void setQualifyingProperties(QualifyingPropertiesType var1);

    public QualifyingPropertiesType addNewQualifyingProperties();
}

