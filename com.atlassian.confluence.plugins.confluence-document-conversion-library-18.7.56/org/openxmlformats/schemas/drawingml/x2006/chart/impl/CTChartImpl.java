/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotFmts
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispBlanksAs;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotFmts;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTView3D;

public class CTChartImpl
extends XmlComplexContentImpl
implements CTChart {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "title"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "autoTitleDeleted"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pivotFmts"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "view3D"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "floor"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "sideWall"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "backWall"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "plotArea"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "legend"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "plotVisOnly"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dispBlanksAs"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showDLblsOverMax"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst")};

    public CTChartImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTitle getTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTitle target = null;
            target = (CTTitle)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setTitle(CTTitle title) {
        this.generatedSetterHelperImpl(title, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTitle addNewTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTitle target = null;
            target = (CTTitle)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean getAutoTitleDeleted() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAutoTitleDeleted() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setAutoTitleDeleted(CTBoolean autoTitleDeleted) {
        this.generatedSetterHelperImpl(autoTitleDeleted, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean addNewAutoTitleDeleted() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAutoTitleDeleted() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPivotFmts getPivotFmts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotFmts target = null;
            target = (CTPivotFmts)this.get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPivotFmts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setPivotFmts(CTPivotFmts pivotFmts) {
        this.generatedSetterHelperImpl((XmlObject)pivotFmts, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPivotFmts addNewPivotFmts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPivotFmts target = null;
            target = (CTPivotFmts)this.get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPivotFmts() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTView3D getView3D() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTView3D target = null;
            target = (CTView3D)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetView3D() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setView3D(CTView3D view3D) {
        this.generatedSetterHelperImpl(view3D, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTView3D addNewView3D() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTView3D target = null;
            target = (CTView3D)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetView3D() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface getFloor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface target = null;
            target = (CTSurface)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFloor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setFloor(CTSurface floor) {
        this.generatedSetterHelperImpl(floor, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface addNewFloor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface target = null;
            target = (CTSurface)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFloor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface getSideWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface target = null;
            target = (CTSurface)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSideWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setSideWall(CTSurface sideWall) {
        this.generatedSetterHelperImpl(sideWall, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface addNewSideWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface target = null;
            target = (CTSurface)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSideWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface getBackWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface target = null;
            target = (CTSurface)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBackWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setBackWall(CTSurface backWall) {
        this.generatedSetterHelperImpl(backWall, PROPERTY_QNAME[6], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSurface addNewBackWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSurface target = null;
            target = (CTSurface)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBackWall() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPlotArea getPlotArea() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPlotArea target = null;
            target = (CTPlotArea)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setPlotArea(CTPlotArea plotArea) {
        this.generatedSetterHelperImpl(plotArea, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPlotArea addNewPlotArea() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPlotArea target = null;
            target = (CTPlotArea)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLegend getLegend() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLegend target = null;
            target = (CTLegend)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLegend() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setLegend(CTLegend legend) {
        this.generatedSetterHelperImpl(legend, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLegend addNewLegend() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLegend target = null;
            target = (CTLegend)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLegend() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean getPlotVisOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPlotVisOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setPlotVisOnly(CTBoolean plotVisOnly) {
        this.generatedSetterHelperImpl(plotVisOnly, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean addNewPlotVisOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPlotVisOnly() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDispBlanksAs getDispBlanksAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDispBlanksAs target = null;
            target = (CTDispBlanksAs)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDispBlanksAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setDispBlanksAs(CTDispBlanksAs dispBlanksAs) {
        this.generatedSetterHelperImpl(dispBlanksAs, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDispBlanksAs addNewDispBlanksAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDispBlanksAs target = null;
            target = (CTDispBlanksAs)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDispBlanksAs() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean getShowDLblsOverMax() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetShowDLblsOverMax() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setShowDLblsOverMax(CTBoolean showDLblsOverMax) {
        this.generatedSetterHelperImpl(showDLblsOverMax, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean addNewShowDLblsOverMax() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetShowDLblsOverMax() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtensionList addNewExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], 0);
        }
    }
}

