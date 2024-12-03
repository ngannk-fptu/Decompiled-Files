/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCellMergeTrackChange
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCellMergeTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;

public interface CTTcPrInner
extends CTTcPrBase {
    public static final DocumentFactory<CTTcPrInner> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttcprinnerc56dtype");
    public static final SchemaType type = Factory.getType();

    public CTTrackChange getCellIns();

    public boolean isSetCellIns();

    public void setCellIns(CTTrackChange var1);

    public CTTrackChange addNewCellIns();

    public void unsetCellIns();

    public CTTrackChange getCellDel();

    public boolean isSetCellDel();

    public void setCellDel(CTTrackChange var1);

    public CTTrackChange addNewCellDel();

    public void unsetCellDel();

    public CTCellMergeTrackChange getCellMerge();

    public boolean isSetCellMerge();

    public void setCellMerge(CTCellMergeTrackChange var1);

    public CTCellMergeTrackChange addNewCellMerge();

    public void unsetCellMerge();
}

