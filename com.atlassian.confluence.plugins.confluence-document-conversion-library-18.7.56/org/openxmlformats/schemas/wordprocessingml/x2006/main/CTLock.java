/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLock;

public interface CTLock
extends XmlObject {
    public static final DocumentFactory<CTLock> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlock201dtype");
    public static final SchemaType type = Factory.getType();

    public STLock.Enum getVal();

    public STLock xgetVal();

    public boolean isSetVal();

    public void setVal(STLock.Enum var1);

    public void xsetVal(STLock var1);

    public void unsetVal();
}

