/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextScale;

public interface CTTextScale
extends XmlObject {
    public static final DocumentFactory<CTTextScale> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextscale3455type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STTextScale xgetVal();

    public boolean isSetVal();

    public void setVal(Object var1);

    public void xsetVal(STTextScale var1);

    public void unsetVal();
}

