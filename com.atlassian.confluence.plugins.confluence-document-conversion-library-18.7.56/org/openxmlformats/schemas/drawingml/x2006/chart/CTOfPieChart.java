/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCustSplit;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGapAmount;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOfPieType;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSecondPieSize;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSplitType;

public interface CTOfPieChart
extends XmlObject {
    public static final DocumentFactory<CTOfPieChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctofpiechartbbb3type");
    public static final SchemaType type = Factory.getType();

    public CTOfPieType getOfPieType();

    public void setOfPieType(CTOfPieType var1);

    public CTOfPieType addNewOfPieType();

    public CTBoolean getVaryColors();

    public boolean isSetVaryColors();

    public void setVaryColors(CTBoolean var1);

    public CTBoolean addNewVaryColors();

    public void unsetVaryColors();

    public List<CTPieSer> getSerList();

    public CTPieSer[] getSerArray();

    public CTPieSer getSerArray(int var1);

    public int sizeOfSerArray();

    public void setSerArray(CTPieSer[] var1);

    public void setSerArray(int var1, CTPieSer var2);

    public CTPieSer insertNewSer(int var1);

    public CTPieSer addNewSer();

    public void removeSer(int var1);

    public CTDLbls getDLbls();

    public boolean isSetDLbls();

    public void setDLbls(CTDLbls var1);

    public CTDLbls addNewDLbls();

    public void unsetDLbls();

    public CTGapAmount getGapWidth();

    public boolean isSetGapWidth();

    public void setGapWidth(CTGapAmount var1);

    public CTGapAmount addNewGapWidth();

    public void unsetGapWidth();

    public CTSplitType getSplitType();

    public boolean isSetSplitType();

    public void setSplitType(CTSplitType var1);

    public CTSplitType addNewSplitType();

    public void unsetSplitType();

    public CTDouble getSplitPos();

    public boolean isSetSplitPos();

    public void setSplitPos(CTDouble var1);

    public CTDouble addNewSplitPos();

    public void unsetSplitPos();

    public CTCustSplit getCustSplit();

    public boolean isSetCustSplit();

    public void setCustSplit(CTCustSplit var1);

    public CTCustSplit addNewCustSplit();

    public void unsetCustSplit();

    public CTSecondPieSize getSecondPieSize();

    public boolean isSetSecondPieSize();

    public void setSecondPieSize(CTSecondPieSize var1);

    public CTSecondPieSize addNewSecondPieSize();

    public void unsetSecondPieSize();

    public List<CTChartLines> getSerLinesList();

    public CTChartLines[] getSerLinesArray();

    public CTChartLines getSerLinesArray(int var1);

    public int sizeOfSerLinesArray();

    public void setSerLinesArray(CTChartLines[] var1);

    public void setSerLinesArray(int var1, CTChartLines var2);

    public CTChartLines insertNewSerLines(int var1);

    public CTChartLines addNewSerLines();

    public void removeSerLines(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

