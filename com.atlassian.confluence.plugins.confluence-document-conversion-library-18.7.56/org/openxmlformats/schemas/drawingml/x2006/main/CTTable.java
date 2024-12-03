/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableGrid;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;

public interface CTTable
extends XmlObject {
    public static final DocumentFactory<CTTable> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttable5f3ftype");
    public static final SchemaType type = Factory.getType();

    public CTTableProperties getTblPr();

    public boolean isSetTblPr();

    public void setTblPr(CTTableProperties var1);

    public CTTableProperties addNewTblPr();

    public void unsetTblPr();

    public CTTableGrid getTblGrid();

    public void setTblGrid(CTTableGrid var1);

    public CTTableGrid addNewTblGrid();

    public List<CTTableRow> getTrList();

    public CTTableRow[] getTrArray();

    public CTTableRow getTrArray(int var1);

    public int sizeOfTrArray();

    public void setTrArray(CTTableRow[] var1);

    public void setTrArray(int var1, CTTableRow var2);

    public CTTableRow insertNewTr(int var1);

    public CTTableRow addNewTr();

    public void removeTr(int var1);
}

