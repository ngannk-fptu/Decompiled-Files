/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercentOrPercentString;

public interface CTTextSpacingPercent
extends XmlObject {
    public static final DocumentFactory<CTTextSpacingPercent> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextspacingpercent322atype");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STTextSpacingPercentOrPercentString xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STTextSpacingPercentOrPercentString var1);
}

