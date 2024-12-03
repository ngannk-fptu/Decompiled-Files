/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTTableCell
extends XmlObject {
    public static final DocumentFactory<CTTableCell> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablecell3ac1type");
    public static final SchemaType type = Factory.getType();

    public CTTextBody getTxBody();

    public boolean isSetTxBody();

    public void setTxBody(CTTextBody var1);

    public CTTextBody addNewTxBody();

    public void unsetTxBody();

    public CTTableCellProperties getTcPr();

    public boolean isSetTcPr();

    public void setTcPr(CTTableCellProperties var1);

    public CTTableCellProperties addNewTcPr();

    public void unsetTcPr();

    public CTOfficeArtExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTOfficeArtExtensionList var1);

    public CTOfficeArtExtensionList addNewExtLst();

    public void unsetExtLst();

    public int getRowSpan();

    public XmlInt xgetRowSpan();

    public boolean isSetRowSpan();

    public void setRowSpan(int var1);

    public void xsetRowSpan(XmlInt var1);

    public void unsetRowSpan();

    public int getGridSpan();

    public XmlInt xgetGridSpan();

    public boolean isSetGridSpan();

    public void setGridSpan(int var1);

    public void xsetGridSpan(XmlInt var1);

    public void unsetGridSpan();

    public boolean getHMerge();

    public XmlBoolean xgetHMerge();

    public boolean isSetHMerge();

    public void setHMerge(boolean var1);

    public void xsetHMerge(XmlBoolean var1);

    public void unsetHMerge();

    public boolean getVMerge();

    public XmlBoolean xgetVMerge();

    public boolean isSetVMerge();

    public void setVMerge(boolean var1);

    public void xsetVMerge(XmlBoolean var1);

    public void unsetVMerge();

    public String getId();

    public XmlString xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(XmlString var1);

    public void unsetId();
}

