/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;

public interface CTPercentage
extends XmlObject {
    public static final DocumentFactory<CTPercentage> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpercentage4e75type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STPercentage xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STPercentage var1);
}

