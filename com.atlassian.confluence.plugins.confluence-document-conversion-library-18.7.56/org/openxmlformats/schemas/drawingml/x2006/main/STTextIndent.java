/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32Unqualified;

public interface STTextIndent
extends STCoordinate32Unqualified {
    public static final SimpleTypeFactory<STTextIndent> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "sttextindent16e4type");
    public static final SchemaType type = Factory.getType();
}

