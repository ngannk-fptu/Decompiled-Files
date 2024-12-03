/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.substwsdl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.metadata.system.sXMLTOOLS.TypeSystemHolder;

public interface TImport
extends XmlObject {
    public static final DocumentFactory<TImport> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "timport22datype");
    public static final SchemaType type = Factory.getType();

    public String getNamespace();

    public XmlAnyURI xgetNamespace();

    public void setNamespace(String var1);

    public void xsetNamespace(XmlAnyURI var1);

    public String getLocation();

    public XmlAnyURI xgetLocation();

    public void setLocation(String var1);

    public void xsetLocation(XmlAnyURI var1);
}

