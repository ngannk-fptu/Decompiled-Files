/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3.x2000.x09.xmldsig.TransformsType
 */
package org.w3.x2000.x09.xmldsig;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.w3.x2000.x09.xmldsig.DigestValueType;
import org.w3.x2000.x09.xmldsig.TransformsType;

public interface ReferenceType
extends XmlObject {
    public static final DocumentFactory<ReferenceType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "referencetypef44ctype");
    public static final SchemaType type = Factory.getType();

    public TransformsType getTransforms();

    public boolean isSetTransforms();

    public void setTransforms(TransformsType var1);

    public TransformsType addNewTransforms();

    public void unsetTransforms();

    public DigestMethodType getDigestMethod();

    public void setDigestMethod(DigestMethodType var1);

    public DigestMethodType addNewDigestMethod();

    public byte[] getDigestValue();

    public DigestValueType xgetDigestValue();

    public void setDigestValue(byte[] var1);

    public void xsetDigestValue(DigestValueType var1);

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();

    public String getURI();

    public XmlAnyURI xgetURI();

    public boolean isSetURI();

    public void setURI(String var1);

    public void xsetURI(XmlAnyURI var1);

    public void unsetURI();

    public String getType();

    public XmlAnyURI xgetType();

    public boolean isSetType();

    public void setType(String var1);

    public void xsetType(XmlAnyURI var1);

    public void unsetType();
}

