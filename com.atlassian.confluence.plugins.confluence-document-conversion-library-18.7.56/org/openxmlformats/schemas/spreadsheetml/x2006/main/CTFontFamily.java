/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontFamily;

public interface CTFontFamily
extends XmlObject {
    public static final DocumentFactory<CTFontFamily> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfontfamily685ctype");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public STFontFamily xgetVal();

    public void setVal(int var1);

    public void xsetVal(STFontFamily var1);
}

