/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.metadata.system.sXMLLANG.TypeSystemHolder;

public interface IdAttribute
extends XmlObject {
    public static final DocumentFactory<IdAttribute> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "idd759attrtypetype");
    public static final SchemaType type = Factory.getType();

    public String getId();

    public XmlID xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlID var1);

    public void unsetId();
}

