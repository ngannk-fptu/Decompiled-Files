/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;

public interface CTSdtText
extends XmlObject {
    public static final DocumentFactory<CTSdtText> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsdttext0a82type");
    public static final SchemaType type = Factory.getType();

    public Object getMultiLine();

    public STOnOff xgetMultiLine();

    public boolean isSetMultiLine();

    public void setMultiLine(Object var1);

    public void xsetMultiLine(STOnOff var1);

    public void unsetMultiLine();
}

