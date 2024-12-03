/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridChange;

public interface CTTblGrid
extends CTTblGridBase {
    public static final DocumentFactory<CTTblGrid> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblgrid2eeetype");
    public static final SchemaType type = Factory.getType();

    public CTTblGridChange getTblGridChange();

    public boolean isSetTblGridChange();

    public void setTblGridChange(CTTblGridChange var1);

    public CTTblGridChange addNewTblGridChange();

    public void unsetTblGridChange();
}

