/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.etsi.uri.x01903.v13.UnsignedDataObjectPropertiesType
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.UnsignedDataObjectPropertiesType;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;

public interface UnsignedPropertiesType
extends XmlObject {
    public static final DocumentFactory<UnsignedPropertiesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "unsignedpropertiestype49d6type");
    public static final SchemaType type = Factory.getType();

    public UnsignedSignaturePropertiesType getUnsignedSignatureProperties();

    public boolean isSetUnsignedSignatureProperties();

    public void setUnsignedSignatureProperties(UnsignedSignaturePropertiesType var1);

    public UnsignedSignaturePropertiesType addNewUnsignedSignatureProperties();

    public void unsetUnsignedSignatureProperties();

    public UnsignedDataObjectPropertiesType getUnsignedDataObjectProperties();

    public boolean isSetUnsignedDataObjectProperties();

    public void setUnsignedDataObjectProperties(UnsignedDataObjectPropertiesType var1);

    public UnsignedDataObjectPropertiesType addNewUnsignedDataObjectProperties();

    public void unsetUnsignedDataObjectProperties();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

