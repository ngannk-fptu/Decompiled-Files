/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;

public interface CTCellWatch
extends XmlObject {
    public static final DocumentFactory<CTCellWatch> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcellwatch3dectype");
    public static final SchemaType type = Factory.getType();

    public String getR();

    public STCellRef xgetR();

    public void setR(String var1);

    public void xsetR(STCellRef var1);
}

