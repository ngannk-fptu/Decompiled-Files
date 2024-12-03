/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;

public interface CTJc
extends XmlObject {
    public static final DocumentFactory<CTJc> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctjc158ftype");
    public static final SchemaType type = Factory.getType();

    public STJc.Enum getVal();

    public STJc xgetVal();

    public void setVal(STJc.Enum var1);

    public void xsetVal(STJc var1);
}

