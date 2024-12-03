/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

public interface CTNumFmt
extends XmlObject {
    public static final DocumentFactory<CTNumFmt> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctnumfmt00e1type");
    public static final SchemaType type = Factory.getType();

    public STNumberFormat.Enum getVal();

    public STNumberFormat xgetVal();

    public void setVal(STNumberFormat.Enum var1);

    public void xsetVal(STNumberFormat var1);

    public String getFormat();

    public STString xgetFormat();

    public boolean isSetFormat();

    public void setFormat(String var1);

    public void xsetFormat(STString var1);

    public void unsetFormat();
}

