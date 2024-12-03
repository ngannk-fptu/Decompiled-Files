/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;

public interface CTTrPr
extends CTTrPrBase {
    public static final DocumentFactory<CTTrPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttrpr2848type");
    public static final SchemaType type = Factory.getType();

    public CTTrackChange getIns();

    public boolean isSetIns();

    public void setIns(CTTrackChange var1);

    public CTTrackChange addNewIns();

    public void unsetIns();

    public CTTrackChange getDel();

    public boolean isSetDel();

    public void setDel(CTTrackChange var1);

    public CTTrackChange addNewDel();

    public void unsetDel();

    public CTTrPrChange getTrPrChange();

    public boolean isSetTrPrChange();

    public void setTrPrChange(CTTrPrChange var1);

    public CTTrPrChange addNewTrPrChange();

    public void unsetTrPrChange();
}

