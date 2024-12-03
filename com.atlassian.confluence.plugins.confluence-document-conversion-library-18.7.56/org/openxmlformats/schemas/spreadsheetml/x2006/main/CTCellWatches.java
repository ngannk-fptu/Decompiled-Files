/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellWatch;

public interface CTCellWatches
extends XmlObject {
    public static final DocumentFactory<CTCellWatches> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcellwatches531atype");
    public static final SchemaType type = Factory.getType();

    public List<CTCellWatch> getCellWatchList();

    public CTCellWatch[] getCellWatchArray();

    public CTCellWatch getCellWatchArray(int var1);

    public int sizeOfCellWatchArray();

    public void setCellWatchArray(CTCellWatch[] var1);

    public void setCellWatchArray(int var1, CTCellWatch var2);

    public CTCellWatch insertNewCellWatch(int var1);

    public CTCellWatch addNewCellWatch();

    public void removeCellWatch(int var1);
}

