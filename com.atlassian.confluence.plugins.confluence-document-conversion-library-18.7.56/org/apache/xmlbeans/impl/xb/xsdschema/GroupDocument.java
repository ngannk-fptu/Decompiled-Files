/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface GroupDocument
extends XmlObject {
    public static final DocumentFactory<GroupDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "group6eb6doctype");
    public static final SchemaType type = Factory.getType();

    public NamedGroup getGroup();

    public void setGroup(NamedGroup var1);

    public NamedGroup addNewGroup();
}

