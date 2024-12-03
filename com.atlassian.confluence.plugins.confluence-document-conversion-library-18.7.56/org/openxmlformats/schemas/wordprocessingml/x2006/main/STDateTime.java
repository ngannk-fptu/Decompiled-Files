/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STDateTime
extends XmlDateTime {
    public static final SimpleTypeFactory<STDateTime> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stdatetimee41dtype");
    public static final SchemaType type = Factory.getType();
}

