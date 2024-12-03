/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;

public interface CTLineJoinMiterProperties
extends XmlObject {
    public static final DocumentFactory<CTLineJoinMiterProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlinejoinmiterproperties02abtype");
    public static final SchemaType type = Factory.getType();

    public Object getLim();

    public STPositivePercentage xgetLim();

    public boolean isSetLim();

    public void setLim(Object var1);

    public void xsetLim(STPositivePercentage var1);

    public void unsetLim();
}

