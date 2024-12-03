/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;

public interface CTPositiveFixedPercentage
extends XmlObject {
    public static final DocumentFactory<CTPositiveFixedPercentage> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpositivefixedpercentage8966type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STPositiveFixedPercentage xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STPositiveFixedPercentage var1);
}

