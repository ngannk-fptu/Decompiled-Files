/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrExChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrExBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrExChange;

public interface CTTblPrEx
extends CTTblPrExBase {
    public static final DocumentFactory<CTTblPrEx> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblprex863ftype");
    public static final SchemaType type = Factory.getType();

    public CTTblPrExChange getTblPrExChange();

    public boolean isSetTblPrExChange();

    public void setTblPrExChange(CTTblPrExChange var1);

    public CTTblPrExChange addNewTblPrExChange();

    public void unsetTblPrExChange();
}

