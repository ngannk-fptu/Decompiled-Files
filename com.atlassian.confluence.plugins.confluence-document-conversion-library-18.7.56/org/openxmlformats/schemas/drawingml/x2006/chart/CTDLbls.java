/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbl
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTDLbls
extends XmlObject {
    public static final DocumentFactory<CTDLbls> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdlblsb585type");
    public static final SchemaType type = Factory.getType();

    public List<CTDLbl> getDLblList();

    public CTDLbl[] getDLblArray();

    public CTDLbl getDLblArray(int var1);

    public int sizeOfDLblArray();

    public void setDLblArray(CTDLbl[] var1);

    public void setDLblArray(int var1, CTDLbl var2);

    public CTDLbl insertNewDLbl(int var1);

    public CTDLbl addNewDLbl();

    public void removeDLbl(int var1);

    public CTBoolean getDelete();

    public boolean isSetDelete();

    public void setDelete(CTBoolean var1);

    public CTBoolean addNewDelete();

    public void unsetDelete();

    public CTNumFmt getNumFmt();

    public boolean isSetNumFmt();

    public void setNumFmt(CTNumFmt var1);

    public CTNumFmt addNewNumFmt();

    public void unsetNumFmt();

    public CTShapeProperties getSpPr();

    public boolean isSetSpPr();

    public void setSpPr(CTShapeProperties var1);

    public CTShapeProperties addNewSpPr();

    public void unsetSpPr();

    public CTTextBody getTxPr();

    public boolean isSetTxPr();

    public void setTxPr(CTTextBody var1);

    public CTTextBody addNewTxPr();

    public void unsetTxPr();

    public CTDLblPos getDLblPos();

    public boolean isSetDLblPos();

    public void setDLblPos(CTDLblPos var1);

    public CTDLblPos addNewDLblPos();

    public void unsetDLblPos();

    public CTBoolean getShowLegendKey();

    public boolean isSetShowLegendKey();

    public void setShowLegendKey(CTBoolean var1);

    public CTBoolean addNewShowLegendKey();

    public void unsetShowLegendKey();

    public CTBoolean getShowVal();

    public boolean isSetShowVal();

    public void setShowVal(CTBoolean var1);

    public CTBoolean addNewShowVal();

    public void unsetShowVal();

    public CTBoolean getShowCatName();

    public boolean isSetShowCatName();

    public void setShowCatName(CTBoolean var1);

    public CTBoolean addNewShowCatName();

    public void unsetShowCatName();

    public CTBoolean getShowSerName();

    public boolean isSetShowSerName();

    public void setShowSerName(CTBoolean var1);

    public CTBoolean addNewShowSerName();

    public void unsetShowSerName();

    public CTBoolean getShowPercent();

    public boolean isSetShowPercent();

    public void setShowPercent(CTBoolean var1);

    public CTBoolean addNewShowPercent();

    public void unsetShowPercent();

    public CTBoolean getShowBubbleSize();

    public boolean isSetShowBubbleSize();

    public void setShowBubbleSize(CTBoolean var1);

    public CTBoolean addNewShowBubbleSize();

    public void unsetShowBubbleSize();

    public String getSeparator();

    public XmlString xgetSeparator();

    public boolean isSetSeparator();

    public void setSeparator(String var1);

    public void xsetSeparator(XmlString var1);

    public void unsetSeparator();

    public CTBoolean getShowLeaderLines();

    public boolean isSetShowLeaderLines();

    public void setShowLeaderLines(CTBoolean var1);

    public CTBoolean addNewShowLeaderLines();

    public void unsetShowLeaderLines();

    public CTChartLines getLeaderLines();

    public boolean isSetLeaderLines();

    public void setLeaderLines(CTChartLines var1);

    public CTChartLines addNewLeaderLines();

    public void unsetLeaderLines();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

