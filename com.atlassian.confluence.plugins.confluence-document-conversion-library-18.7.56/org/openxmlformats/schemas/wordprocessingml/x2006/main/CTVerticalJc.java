/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

public interface CTVerticalJc
extends XmlObject {
    public static final DocumentFactory<CTVerticalJc> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctverticaljca439type");
    public static final SchemaType type = Factory.getType();

    public STVerticalJc.Enum getVal();

    public STVerticalJc xgetVal();

    public void setVal(STVerticalJc.Enum var1);

    public void xsetVal(STVerticalJc var1);
}

