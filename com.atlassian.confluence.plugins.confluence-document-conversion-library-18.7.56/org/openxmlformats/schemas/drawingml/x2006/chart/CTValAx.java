/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTDispUnits
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxisUnit;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrossBetween;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispUnits;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTValAx
extends XmlObject {
    public static final DocumentFactory<CTValAx> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctvalaxd06etype");
    public static final SchemaType type = Factory.getType();

    public CTUnsignedInt getAxId();

    public void setAxId(CTUnsignedInt var1);

    public CTUnsignedInt addNewAxId();

    public CTScaling getScaling();

    public void setScaling(CTScaling var1);

    public CTScaling addNewScaling();

    public CTBoolean getDelete();

    public boolean isSetDelete();

    public void setDelete(CTBoolean var1);

    public CTBoolean addNewDelete();

    public void unsetDelete();

    public CTAxPos getAxPos();

    public void setAxPos(CTAxPos var1);

    public CTAxPos addNewAxPos();

    public CTChartLines getMajorGridlines();

    public boolean isSetMajorGridlines();

    public void setMajorGridlines(CTChartLines var1);

    public CTChartLines addNewMajorGridlines();

    public void unsetMajorGridlines();

    public CTChartLines getMinorGridlines();

    public boolean isSetMinorGridlines();

    public void setMinorGridlines(CTChartLines var1);

    public CTChartLines addNewMinorGridlines();

    public void unsetMinorGridlines();

    public CTTitle getTitle();

    public boolean isSetTitle();

    public void setTitle(CTTitle var1);

    public CTTitle addNewTitle();

    public void unsetTitle();

    public CTNumFmt getNumFmt();

    public boolean isSetNumFmt();

    public void setNumFmt(CTNumFmt var1);

    public CTNumFmt addNewNumFmt();

    public void unsetNumFmt();

    public CTTickMark getMajorTickMark();

    public boolean isSetMajorTickMark();

    public void setMajorTickMark(CTTickMark var1);

    public CTTickMark addNewMajorTickMark();

    public void unsetMajorTickMark();

    public CTTickMark getMinorTickMark();

    public boolean isSetMinorTickMark();

    public void setMinorTickMark(CTTickMark var1);

    public CTTickMark addNewMinorTickMark();

    public void unsetMinorTickMark();

    public CTTickLblPos getTickLblPos();

    public boolean isSetTickLblPos();

    public void setTickLblPos(CTTickLblPos var1);

    public CTTickLblPos addNewTickLblPos();

    public void unsetTickLblPos();

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

    public CTUnsignedInt getCrossAx();

    public void setCrossAx(CTUnsignedInt var1);

    public CTUnsignedInt addNewCrossAx();

    public CTCrosses getCrosses();

    public boolean isSetCrosses();

    public void setCrosses(CTCrosses var1);

    public CTCrosses addNewCrosses();

    public void unsetCrosses();

    public CTDouble getCrossesAt();

    public boolean isSetCrossesAt();

    public void setCrossesAt(CTDouble var1);

    public CTDouble addNewCrossesAt();

    public void unsetCrossesAt();

    public CTCrossBetween getCrossBetween();

    public boolean isSetCrossBetween();

    public void setCrossBetween(CTCrossBetween var1);

    public CTCrossBetween addNewCrossBetween();

    public void unsetCrossBetween();

    public CTAxisUnit getMajorUnit();

    public boolean isSetMajorUnit();

    public void setMajorUnit(CTAxisUnit var1);

    public CTAxisUnit addNewMajorUnit();

    public void unsetMajorUnit();

    public CTAxisUnit getMinorUnit();

    public boolean isSetMinorUnit();

    public void setMinorUnit(CTAxisUnit var1);

    public CTAxisUnit addNewMinorUnit();

    public void unsetMinorUnit();

    public CTDispUnits getDispUnits();

    public boolean isSetDispUnits();

    public void setDispUnits(CTDispUnits var1);

    public CTDispUnits addNewDispUnits();

    public void unsetDispUnits();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

