/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFtnPos;

public interface CTFtnPos
extends XmlObject {
    public static final DocumentFactory<CTFtnPos> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctftnposd254type");
    public static final SchemaType type = Factory.getType();

    public STFtnPos.Enum getVal();

    public STFtnPos xgetVal();

    public void setVal(STFtnPos.Enum var1);

    public void xsetVal(STFtnPos var1);
}

