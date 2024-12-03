/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontScheme;

public interface CTFontScheme
extends XmlObject {
    public static final DocumentFactory<CTFontScheme> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfontschemebf5dtype");
    public static final SchemaType type = Factory.getType();

    public STFontScheme.Enum getVal();

    public STFontScheme xgetVal();

    public void setVal(STFontScheme.Enum var1);

    public void xsetVal(STFontScheme var1);
}

