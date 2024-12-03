/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.SignedPropertiesType;
import org.etsi.uri.x01903.v13.UnsignedPropertiesType;

public interface QualifyingPropertiesType
extends XmlObject {
    public static final DocumentFactory<QualifyingPropertiesType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "qualifyingpropertiestype9e16type");
    public static final SchemaType type = Factory.getType();

    public SignedPropertiesType getSignedProperties();

    public boolean isSetSignedProperties();

    public void setSignedProperties(SignedPropertiesType var1);

    public SignedPropertiesType addNewSignedProperties();

    public void unsetSignedProperties();

    public UnsignedPropertiesType getUnsignedProperties();

    public boolean isSetUnsignedProperties();

    public void setUnsignedProperties(UnsignedPropertiesType var1);

    public UnsignedPropertiesType addNewUnsignedProperties();

    public void unsetUnsignedProperties();

    public String getTarget();

    public XmlAnyURI xgetTarget();

    public void setTarget(String var1);

    public void xsetTarget(XmlAnyURI var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

