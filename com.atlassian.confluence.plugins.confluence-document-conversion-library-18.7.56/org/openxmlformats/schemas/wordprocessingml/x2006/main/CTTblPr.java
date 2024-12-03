/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPrChange;

public interface CTTblPr
extends CTTblPrBase {
    public static final DocumentFactory<CTTblPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttblpr5b72type");
    public static final SchemaType type = Factory.getType();

    public CTTblPrChange getTblPrChange();

    public boolean isSetTblPrChange();

    public void setTblPrChange(CTTblPrChange var1);

    public CTTblPrChange addNewTblPrChange();

    public void unsetTblPrChange();
}

