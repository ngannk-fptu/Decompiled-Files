/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTSkip
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSkip;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public interface CTSerAx
extends XmlObject {
    public static final DocumentFactory<CTSerAx> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctserax2c0ftype");
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

    public CTSkip getTickLblSkip();

    public boolean isSetTickLblSkip();

    public void setTickLblSkip(CTSkip var1);

    public CTSkip addNewTickLblSkip();

    public void unsetTickLblSkip();

    public CTSkip getTickMarkSkip();

    public boolean isSetTickMarkSkip();

    public void setTickMarkSkip(CTSkip var1);

    public CTSkip addNewTickMarkSkip();

    public void unsetTickMarkSkip();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

