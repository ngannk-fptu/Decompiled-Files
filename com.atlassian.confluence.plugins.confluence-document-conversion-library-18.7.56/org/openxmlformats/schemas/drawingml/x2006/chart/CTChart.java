/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotFmts
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispBlanksAs;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotFmts;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTView3D;

public interface CTChart
extends XmlObject {
    public static final DocumentFactory<CTChart> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctchartc108type");
    public static final SchemaType type = Factory.getType();

    public CTTitle getTitle();

    public boolean isSetTitle();

    public void setTitle(CTTitle var1);

    public CTTitle addNewTitle();

    public void unsetTitle();

    public CTBoolean getAutoTitleDeleted();

    public boolean isSetAutoTitleDeleted();

    public void setAutoTitleDeleted(CTBoolean var1);

    public CTBoolean addNewAutoTitleDeleted();

    public void unsetAutoTitleDeleted();

    public CTPivotFmts getPivotFmts();

    public boolean isSetPivotFmts();

    public void setPivotFmts(CTPivotFmts var1);

    public CTPivotFmts addNewPivotFmts();

    public void unsetPivotFmts();

    public CTView3D getView3D();

    public boolean isSetView3D();

    public void setView3D(CTView3D var1);

    public CTView3D addNewView3D();

    public void unsetView3D();

    public CTSurface getFloor();

    public boolean isSetFloor();

    public void setFloor(CTSurface var1);

    public CTSurface addNewFloor();

    public void unsetFloor();

    public CTSurface getSideWall();

    public boolean isSetSideWall();

    public void setSideWall(CTSurface var1);

    public CTSurface addNewSideWall();

    public void unsetSideWall();

    public CTSurface getBackWall();

    public boolean isSetBackWall();

    public void setBackWall(CTSurface var1);

    public CTSurface addNewBackWall();

    public void unsetBackWall();

    public CTPlotArea getPlotArea();

    public void setPlotArea(CTPlotArea var1);

    public CTPlotArea addNewPlotArea();

    public CTLegend getLegend();

    public boolean isSetLegend();

    public void setLegend(CTLegend var1);

    public CTLegend addNewLegend();

    public void unsetLegend();

    public CTBoolean getPlotVisOnly();

    public boolean isSetPlotVisOnly();

    public void setPlotVisOnly(CTBoolean var1);

    public CTBoolean addNewPlotVisOnly();

    public void unsetPlotVisOnly();

    public CTDispBlanksAs getDispBlanksAs();

    public boolean isSetDispBlanksAs();

    public void setDispBlanksAs(CTDispBlanksAs var1);

    public CTDispBlanksAs addNewDispBlanksAs();

    public void unsetDispBlanksAs();

    public CTBoolean getShowDLblsOverMax();

    public boolean isSetShowDLblsOverMax();

    public void setShowDLblsOverMax(CTBoolean var1);

    public CTBoolean addNewShowDLblsOverMax();

    public void unsetShowDLblsOverMax();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

