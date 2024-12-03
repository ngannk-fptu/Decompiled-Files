/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

public interface CTVMerge
extends XmlObject {
    public static final DocumentFactory<CTVMerge> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctvmergee086type");
    public static final SchemaType type = Factory.getType();

    public STMerge.Enum getVal();

    public STMerge xgetVal();

    public boolean isSetVal();

    public void setVal(STMerge.Enum var1);

    public void xsetVal(STMerge var1);

    public void unsetVal();
}

