/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.math.STInteger255;

public interface CTInteger255
extends XmlObject {
    public static final DocumentFactory<CTInteger255> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctinteger255c19etype");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public STInteger255 xgetVal();

    public void setVal(int var1);

    public void xsetVal(STInteger255 var1);
}

