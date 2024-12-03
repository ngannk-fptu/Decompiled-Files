/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.Group;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ExplicitGroup
extends Group {
    public static final DocumentFactory<ExplicitGroup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "explicitgroup4efatype");
    public static final SchemaType type = Factory.getType();
}

