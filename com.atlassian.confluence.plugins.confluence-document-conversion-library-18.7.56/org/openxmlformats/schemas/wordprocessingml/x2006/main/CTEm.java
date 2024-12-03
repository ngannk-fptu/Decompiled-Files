/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEm;

public interface CTEm
extends XmlObject {
    public static final DocumentFactory<CTEm> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctemdc80type");
    public static final SchemaType type = Factory.getType();

    public STEm.Enum getVal();

    public STEm xgetVal();

    public void setVal(STEm.Enum var1);

    public void xsetVal(STEm var1);
}

