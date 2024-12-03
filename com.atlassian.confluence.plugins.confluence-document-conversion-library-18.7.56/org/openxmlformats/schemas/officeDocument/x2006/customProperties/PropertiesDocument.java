/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.customProperties;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties;

public interface PropertiesDocument
extends XmlObject {
    public static final DocumentFactory<PropertiesDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "properties288cdoctype");
    public static final SchemaType type = Factory.getType();

    public CTProperties getProperties();

    public void setProperties(CTProperties var1);

    public CTProperties addNewProperties();
}

