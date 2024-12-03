/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionType;
import org.apache.xmlbeans.metadata.system.sXMLSCHEMA.TypeSystemHolder;

public interface ComplexRestrictionType
extends RestrictionType {
    public static final DocumentFactory<ComplexRestrictionType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "complexrestrictiontype1b7dtype");
    public static final SchemaType type = Factory.getType();
}

