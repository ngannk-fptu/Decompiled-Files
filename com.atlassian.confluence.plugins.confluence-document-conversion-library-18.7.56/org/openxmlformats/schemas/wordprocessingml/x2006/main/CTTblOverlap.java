/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblOverlap;

public interface CTTblOverlap
extends XmlObject {
    public static final DocumentFactory<CTTblOverlap> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttbloverlap231ftype");
    public static final SchemaType type = Factory.getType();

    public STTblOverlap.Enum getVal();

    public STTblOverlap xgetVal();

    public void setVal(STTblOverlap.Enum var1);

    public void xsetVal(STTblOverlap var1);
}

