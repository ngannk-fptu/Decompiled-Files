/*
 * Decompiled with CFR 0.152.
 */
package org.etsi.uri.x01903.v13;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.etsi.uri.x01903.v13.GenericTimeStampType;

public interface XAdESTimeStampType
extends GenericTimeStampType {
    public static final DocumentFactory<XAdESTimeStampType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "xadestimestamptypeaedbtype");
    public static final SchemaType type = Factory.getType();
}

