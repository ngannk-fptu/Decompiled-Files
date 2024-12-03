/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;

public interface CTFontName
extends XmlObject {
    public static final DocumentFactory<CTFontName> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfontname2dc3type");
    public static final SchemaType type = Factory.getType();

    public String getVal();

    public STXstring xgetVal();

    public void setVal(String var1);

    public void xsetVal(STXstring var1);
}

