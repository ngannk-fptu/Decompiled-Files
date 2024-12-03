/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Keybase;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface KeyDocument
extends XmlObject {
    public static final DocumentFactory<KeyDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "key5d16doctype");
    public static final SchemaType type = Factory.getType();

    public Keybase getKey();

    public void setKey(Keybase var1);

    public Keybase addNewKey();
}

