/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v14;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v14.ValidationDataType;

public interface TimeStampValidationDataDocument
extends XmlObject {
    public static final DocumentFactory<TimeStampValidationDataDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "timestampvalidationdataeb4bdoctype");
    public static final SchemaType type = Factory.getType();

    public ValidationDataType getTimeStampValidationData();

    public void setTimeStampValidationData(ValidationDataType var1);

    public ValidationDataType addNewTimeStampValidationData();
}

