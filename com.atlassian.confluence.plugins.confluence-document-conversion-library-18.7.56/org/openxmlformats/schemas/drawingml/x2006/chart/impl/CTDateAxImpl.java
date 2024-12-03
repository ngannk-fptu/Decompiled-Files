/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTLblOffset
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTTimeUnit
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxisUnit;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLblOffset;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTimeUnit;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;

public class CTDateAxImpl
extends XmlComplexContentImpl
implements CTDateAx {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "scaling"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "delete"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axPos"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorGridlines"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorGridlines"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "title"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numFmt"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorTickMark"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorTickMark"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tickLblPos"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "txPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crossAx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crosses"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crossesAt"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "auto"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "lblOffset"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "baseTimeUnit"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorUnit"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorTimeUnit"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorUnit"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorTimeUnit"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst")};

    public CTDateAxImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt getAxId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAxId(CTUnsignedInt axId) {
        this.generatedSetterHelperImpl(axId, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt addNewAxId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScaling getScaling() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScaling target = null;
            target = (CTScaling)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setScaling(CTScaling scaling) {
        this.generatedSetterHelperImpl(scaling, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTScaling addNewScaling() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTScaling target = null;
            target = (CTScaling)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean getDelete() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDelete() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setDelete(CTBoolean delete) {
        this.generatedSetterHelperImpl(delete, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean addNewDelete() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDelete() {
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
    public CTAxPos getAxPos() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxPos target = null;
            target = (CTAxPos)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setAxPos(CTAxPos axPos) {
        this.generatedSetterHelperImpl(axPos, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAxPos addNewAxPos() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxPos target = null;
            target = (CTAxPos)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartLines getMajorGridlines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartLines target = null;
            target = (CTChartLines)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMajorGridlines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setMajorGridlines(CTChartLines majorGridlines) {
        this.generatedSetterHelperImpl(majorGridlines, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartLines addNewMajorGridlines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartLines target = null;
            target = (CTChartLines)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMajorGridlines() {
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
    public CTChartLines getMinorGridlines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartLines target = null;
            target = (CTChartLines)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMinorGridlines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setMinorGridlines(CTChartLines minorGridlines) {
        this.generatedSetterHelperImpl(minorGridlines, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTChartLines addNewMinorGridlines() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTChartLines target = null;
            target = (CTChartLines)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMinorGridlines() {
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
    public CTTitle getTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTitle target = null;
            target = (CTTitle)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    @Override
    public void setTitle(CTTitle title) {
        this.generatedSetterHelperImpl(title, PROPERTY_QNAME[6], 0, (short)1);
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
            target = (CTTitle)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
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
            this.get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNumFmt getNumFmt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumFmt target = null;
            target = (CTNumFmt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetNumFmt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setNumFmt(CTNumFmt numFmt) {
        this.generatedSetterHelperImpl(numFmt, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNumFmt addNewNumFmt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumFmt target = null;
            target = (CTNumFmt)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetNumFmt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTickMark getMajorTickMark() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTickMark target = null;
            target = (CTTickMark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMajorTickMark() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    @Override
    public void setMajorTickMark(CTTickMark majorTickMark) {
        this.generatedSetterHelperImpl(majorTickMark, PROPERTY_QNAME[8], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTickMark addNewMajorTickMark() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTickMark target = null;
            target = (CTTickMark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMajorTickMark() {
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
    public CTTickMark getMinorTickMark() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTickMark target = null;
            target = (CTTickMark)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMinorTickMark() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setMinorTickMark(CTTickMark minorTickMark) {
        this.generatedSetterHelperImpl(minorTickMark, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTickMark addNewMinorTickMark() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTickMark target = null;
            target = (CTTickMark)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMinorTickMark() {
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
    public CTTickLblPos getTickLblPos() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTickLblPos target = null;
            target = (CTTickLblPos)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTickLblPos() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setTickLblPos(CTTickLblPos tickLblPos) {
        this.generatedSetterHelperImpl(tickLblPos, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTickLblPos addNewTickLblPos() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTickLblPos target = null;
            target = (CTTickLblPos)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTickLblPos() {
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
    public CTShapeProperties getSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeProperties target = null;
            target = (CTShapeProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setSpPr(CTShapeProperties spPr) {
        this.generatedSetterHelperImpl(spPr, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShapeProperties addNewSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeProperties target = null;
            target = (CTShapeProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSpPr() {
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
    public CTTextBody getTxPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextBody target = null;
            target = (CTTextBody)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTxPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setTxPr(CTTextBody txPr) {
        this.generatedSetterHelperImpl(txPr, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextBody addNewTxPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextBody target = null;
            target = (CTTextBody)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTxPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt getCrossAx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setCrossAx(CTUnsignedInt crossAx) {
        this.generatedSetterHelperImpl(crossAx, PROPERTY_QNAME[13], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt addNewCrossAx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCrosses getCrosses() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCrosses target = null;
            target = (CTCrosses)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCrosses() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]) != 0;
        }
    }

    @Override
    public void setCrosses(CTCrosses crosses) {
        this.generatedSetterHelperImpl(crosses, PROPERTY_QNAME[14], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCrosses addNewCrosses() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCrosses target = null;
            target = (CTCrosses)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCrosses() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDouble getCrossesAt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDouble target = null;
            target = (CTDouble)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCrossesAt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]) != 0;
        }
    }

    @Override
    public void setCrossesAt(CTDouble crossesAt) {
        this.generatedSetterHelperImpl(crossesAt, PROPERTY_QNAME[15], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDouble addNewCrossesAt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDouble target = null;
            target = (CTDouble)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCrossesAt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean getAuto() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAuto() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]) != 0;
        }
    }

    @Override
    public void setAuto(CTBoolean auto) {
        this.generatedSetterHelperImpl(auto, PROPERTY_QNAME[16], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean addNewAuto() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAuto() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLblOffset getLblOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLblOffset target = null;
            target = (CTLblOffset)this.get_store().find_element_user(PROPERTY_QNAME[17], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLblOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    @Override
    public void setLblOffset(CTLblOffset lblOffset) {
        this.generatedSetterHelperImpl((XmlObject)lblOffset, PROPERTY_QNAME[17], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLblOffset addNewLblOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLblOffset target = null;
            target = (CTLblOffset)this.get_store().add_element_user(PROPERTY_QNAME[17]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLblOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeUnit getBaseTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeUnit target = null;
            target = (CTTimeUnit)this.get_store().find_element_user(PROPERTY_QNAME[18], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBaseTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]) != 0;
        }
    }

    @Override
    public void setBaseTimeUnit(CTTimeUnit baseTimeUnit) {
        this.generatedSetterHelperImpl((XmlObject)baseTimeUnit, PROPERTY_QNAME[18], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeUnit addNewBaseTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeUnit target = null;
            target = (CTTimeUnit)this.get_store().add_element_user(PROPERTY_QNAME[18]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBaseTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAxisUnit getMajorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxisUnit target = null;
            target = (CTAxisUnit)((Object)this.get_store().find_element_user(PROPERTY_QNAME[19], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMajorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]) != 0;
        }
    }

    @Override
    public void setMajorUnit(CTAxisUnit majorUnit) {
        this.generatedSetterHelperImpl(majorUnit, PROPERTY_QNAME[19], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAxisUnit addNewMajorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxisUnit target = null;
            target = (CTAxisUnit)((Object)this.get_store().add_element_user(PROPERTY_QNAME[19]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMajorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeUnit getMajorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeUnit target = null;
            target = (CTTimeUnit)this.get_store().find_element_user(PROPERTY_QNAME[20], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMajorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]) != 0;
        }
    }

    @Override
    public void setMajorTimeUnit(CTTimeUnit majorTimeUnit) {
        this.generatedSetterHelperImpl((XmlObject)majorTimeUnit, PROPERTY_QNAME[20], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeUnit addNewMajorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeUnit target = null;
            target = (CTTimeUnit)this.get_store().add_element_user(PROPERTY_QNAME[20]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMajorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[20], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAxisUnit getMinorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxisUnit target = null;
            target = (CTAxisUnit)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMinorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]) != 0;
        }
    }

    @Override
    public void setMinorUnit(CTAxisUnit minorUnit) {
        this.generatedSetterHelperImpl(minorUnit, PROPERTY_QNAME[21], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAxisUnit addNewMinorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxisUnit target = null;
            target = (CTAxisUnit)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMinorUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeUnit getMinorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeUnit target = null;
            target = (CTTimeUnit)this.get_store().find_element_user(PROPERTY_QNAME[22], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetMinorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]) != 0;
        }
    }

    @Override
    public void setMinorTimeUnit(CTTimeUnit minorTimeUnit) {
        this.generatedSetterHelperImpl((XmlObject)minorTimeUnit, PROPERTY_QNAME[22], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTimeUnit addNewMinorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTimeUnit target = null;
            target = (CTTimeUnit)this.get_store().add_element_user(PROPERTY_QNAME[22]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetMinorTimeUnit() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[22], 0);
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
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[23], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[23]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[23], 0, (short)1);
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
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[23]));
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
            this.get_store().remove_element(PROPERTY_QNAME[23], 0);
        }
    }
}

