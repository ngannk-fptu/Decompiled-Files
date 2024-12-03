/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

public interface CTHighlight
extends XmlObject {
    public static final DocumentFactory<CTHighlight> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthighlight071etype");
    public static final SchemaType type = Factory.getType();

    public STHighlightColor.Enum getVal();

    public STHighlightColor xgetVal();

    public void setVal(STHighlightColor.Enum var1);

    public void xsetVal(STHighlightColor var1);
}

