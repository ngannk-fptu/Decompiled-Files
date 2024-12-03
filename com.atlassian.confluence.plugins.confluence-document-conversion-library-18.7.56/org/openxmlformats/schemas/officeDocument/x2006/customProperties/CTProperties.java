/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.customProperties;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;

public interface CTProperties
extends XmlObject {
    public static final DocumentFactory<CTProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctproperties2c18type");
    public static final SchemaType type = Factory.getType();

    public List<CTProperty> getPropertyList();

    public CTProperty[] getPropertyArray();

    public CTProperty getPropertyArray(int var1);

    public int sizeOfPropertyArray();

    public void setPropertyArray(CTProperty[] var1);

    public void setPropertyArray(int var1, CTProperty var2);

    public CTProperty insertNewProperty(int var1);

    public CTProperty addNewProperty();

    public void removeProperty(int var1);
}

