/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlColumnPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTotalsRowFunction;

public interface CTTableColumn
extends XmlObject {
    public static final DocumentFactory<CTTableColumn> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttablecolumn08a3type");
    public static final SchemaType type = Factory.getType();

    public CTTableFormula getCalculatedColumnFormula();

    public boolean isSetCalculatedColumnFormula();

    public void setCalculatedColumnFormula(CTTableFormula var1);

    public CTTableFormula addNewCalculatedColumnFormula();

    public void unsetCalculatedColumnFormula();

    public CTTableFormula getTotalsRowFormula();

    public boolean isSetTotalsRowFormula();

    public void setTotalsRowFormula(CTTableFormula var1);

    public CTTableFormula addNewTotalsRowFormula();

    public void unsetTotalsRowFormula();

    public CTXmlColumnPr getXmlColumnPr();

    public boolean isSetXmlColumnPr();

    public void setXmlColumnPr(CTXmlColumnPr var1);

    public CTXmlColumnPr addNewXmlColumnPr();

    public void unsetXmlColumnPr();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public XmlUnsignedInt xgetId();

    public void setId(long var1);

    public void xsetId(XmlUnsignedInt var1);

    public String getUniqueName();

    public STXstring xgetUniqueName();

    public boolean isSetUniqueName();

    public void setUniqueName(String var1);

    public void xsetUniqueName(STXstring var1);

    public void unsetUniqueName();

    public String getName();

    public STXstring xgetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public STTotalsRowFunction.Enum getTotalsRowFunction();

    public STTotalsRowFunction xgetTotalsRowFunction();

    public boolean isSetTotalsRowFunction();

    public void setTotalsRowFunction(STTotalsRowFunction.Enum var1);

    public void xsetTotalsRowFunction(STTotalsRowFunction var1);

    public void unsetTotalsRowFunction();

    public String getTotalsRowLabel();

    public STXstring xgetTotalsRowLabel();

    public boolean isSetTotalsRowLabel();

    public void setTotalsRowLabel(String var1);

    public void xsetTotalsRowLabel(STXstring var1);

    public void unsetTotalsRowLabel();

    public long getQueryTableFieldId();

    public XmlUnsignedInt xgetQueryTableFieldId();

    public boolean isSetQueryTableFieldId();

    public void setQueryTableFieldId(long var1);

    public void xsetQueryTableFieldId(XmlUnsignedInt var1);

    public void unsetQueryTableFieldId();

    public long getHeaderRowDxfId();

    public STDxfId xgetHeaderRowDxfId();

    public boolean isSetHeaderRowDxfId();

    public void setHeaderRowDxfId(long var1);

    public void xsetHeaderRowDxfId(STDxfId var1);

    public void unsetHeaderRowDxfId();

    public long getDataDxfId();

    public STDxfId xgetDataDxfId();

    public boolean isSetDataDxfId();

    public void setDataDxfId(long var1);

    public void xsetDataDxfId(STDxfId var1);

    public void unsetDataDxfId();

    public long getTotalsRowDxfId();

    public STDxfId xgetTotalsRowDxfId();

    public boolean isSetTotalsRowDxfId();

    public void setTotalsRowDxfId(long var1);

    public void xsetTotalsRowDxfId(STDxfId var1);

    public void unsetTotalsRowDxfId();

    public String getHeaderRowCellStyle();

    public STXstring xgetHeaderRowCellStyle();

    public boolean isSetHeaderRowCellStyle();

    public void setHeaderRowCellStyle(String var1);

    public void xsetHeaderRowCellStyle(STXstring var1);

    public void unsetHeaderRowCellStyle();

    public String getDataCellStyle();

    public STXstring xgetDataCellStyle();

    public boolean isSetDataCellStyle();

    public void setDataCellStyle(String var1);

    public void xsetDataCellStyle(STXstring var1);

    public void unsetDataCellStyle();

    public String getTotalsRowCellStyle();

    public STXstring xgetTotalsRowCellStyle();

    public boolean isSetTotalsRowCellStyle();

    public void setTotalsRowCellStyle(String var1);

    public void xsetTotalsRowCellStyle(STXstring var1);

    public void unsetTotalsRowCellStyle();
}

