/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface SimpleExplicitGroup
extends ExplicitGroup {
    public static final DocumentFactory<SimpleExplicitGroup> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "simpleexplicitgroup428ctype");
    public static final SchemaType type = Factory.getType();
}

