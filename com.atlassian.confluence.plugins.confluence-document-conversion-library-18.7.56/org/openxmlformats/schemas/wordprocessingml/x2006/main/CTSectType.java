/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;

public interface CTSectType
extends XmlObject {
    public static final DocumentFactory<CTSectType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsecttype7cebtype");
    public static final SchemaType type = Factory.getType();

    public STSectionMark.Enum getVal();

    public STSectionMark xgetVal();

    public boolean isSetVal();

    public void setVal(STSectionMark.Enum var1);

    public void xsetVal(STSectionMark var1);

    public void unsetVal();
}

