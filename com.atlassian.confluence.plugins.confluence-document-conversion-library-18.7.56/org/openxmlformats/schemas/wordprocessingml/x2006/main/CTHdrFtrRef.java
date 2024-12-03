/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;

public interface CTHdrFtrRef
extends CTRel {
    public static final DocumentFactory<CTHdrFtrRef> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cthdrftrref224dtype");
    public static final SchemaType type = Factory.getType();

    public STHdrFtr.Enum getType();

    public STHdrFtr xgetType();

    public void setType(STHdrFtr.Enum var1);

    public void xsetType(STHdrFtr var1);
}

