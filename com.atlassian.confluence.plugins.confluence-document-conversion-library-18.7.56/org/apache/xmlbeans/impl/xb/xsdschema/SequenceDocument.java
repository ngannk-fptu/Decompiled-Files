/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SequenceDocument
extends XmlObject {
    public static final DocumentFactory<SequenceDocument> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "sequencecba2doctype");
    public static final SchemaType type = Factory.getType();

    public ExplicitGroup getSequence();

    public void setSequence(ExplicitGroup var1);

    public ExplicitGroup addNewSequence();
}

