/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.metadata.system.sXMLLANG.TypeSystemHolder;

public interface BaseAttribute
extends XmlObject {
    public static final DocumentFactory<BaseAttribute> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "basece23attrtypetype");
    public static final SchemaType type = Factory.getType();

    public String getBase();

    public XmlAnyURI xgetBase();

    public boolean isSetBase();

    public void setBase(String var1);

    public void xsetBase(XmlAnyURI var1);

    public void unsetBase();
}

