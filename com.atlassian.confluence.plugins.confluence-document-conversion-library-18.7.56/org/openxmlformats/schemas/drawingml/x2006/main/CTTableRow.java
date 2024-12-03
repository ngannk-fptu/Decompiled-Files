/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;

public interface CTTableRow
extends XmlObject {
    public static final DocumentFactory<CTTableRow> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablerow4ac7type");
    public static final SchemaType type = Factory.getType();

    public List<CTTableCell> getTcList();

    public CTTableCell[] getTcArray();

    public CTTableCell getTcArray(int var1);

    public int sizeOfTcArray();

    public void setTcArray(CTTableCell[] var1);

    public void setTcArray(int var1, CTTableCell var2);

    public CTTableCell insertNewTc(int var1);

    public CTTableCell addNewTc();

    public void removeTc(int var1);

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public Object getH();

    public STCoordinate xgetH();

    public void setH(Object var1);

    public void xsetH(STCoordinate var1);
}

