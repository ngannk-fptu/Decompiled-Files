/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions
 *  org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBars;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

public class CTBarSerImpl
extends XmlComplexContentImpl
implements CTBarSer {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "idx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "order"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "invertIfNegative"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pictureOptions"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dPt"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "trendline"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errBars"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "cat"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "shape"), new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst")};

    public CTBarSerImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt getIdx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setIdx(CTUnsignedInt idx) {
        this.generatedSetterHelperImpl(idx, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt addNewIdx() {
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
    public CTUnsignedInt getOrder() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setOrder(CTUnsignedInt order) {
        this.generatedSetterHelperImpl(order, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTUnsignedInt addNewOrder() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTUnsignedInt target = null;
            target = (CTUnsignedInt)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSerTx getTx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSerTx target = null;
            target = (CTSerTx)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setTx(CTSerTx tx) {
        this.generatedSetterHelperImpl(tx, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSerTx addNewTx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSerTx target = null;
            target = (CTSerTx)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTx() {
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
    public CTShapeProperties getSpPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShapeProperties target = null;
            target = (CTShapeProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setSpPr(CTShapeProperties spPr) {
        this.generatedSetterHelperImpl(spPr, PROPERTY_QNAME[3], 0, (short)1);
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
            target = (CTShapeProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
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
            this.get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean getInvertIfNegative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetInvertIfNegative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setInvertIfNegative(CTBoolean invertIfNegative) {
        this.generatedSetterHelperImpl(invertIfNegative, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBoolean addNewInvertIfNegative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBoolean target = null;
            target = (CTBoolean)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetInvertIfNegative() {
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
    public CTPictureOptions getPictureOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPictureOptions target = null;
            target = (CTPictureOptions)this.get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPictureOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    @Override
    public void setPictureOptions(CTPictureOptions pictureOptions) {
        this.generatedSetterHelperImpl((XmlObject)pictureOptions, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPictureOptions addNewPictureOptions() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPictureOptions target = null;
            target = (CTPictureOptions)this.get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPictureOptions() {
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
    public List<CTDPt> getDPtList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTDPt>(this::getDPtArray, this::setDPtArray, this::insertNewDPt, this::removeDPt, this::sizeOfDPtArray);
        }
    }

    @Override
    public CTDPt[] getDPtArray() {
        return (CTDPt[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTDPt[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDPt getDPtArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDPt target = null;
            target = (CTDPt)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfDPtArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setDPtArray(CTDPt[] dPtArray) {
        this.check_orphaned();
        this.arraySetterHelper(dPtArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setDPtArray(int i, CTDPt dPt) {
        this.generatedSetterHelperImpl(dPt, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDPt insertNewDPt(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDPt target = null;
            target = (CTDPt)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDPt addNewDPt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDPt target = null;
            target = (CTDPt)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeDPt(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDLbls getDLbls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDLbls target = null;
            target = (CTDLbls)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDLbls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]) != 0;
        }
    }

    @Override
    public void setDLbls(CTDLbls dLbls) {
        this.generatedSetterHelperImpl(dLbls, PROPERTY_QNAME[7], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTDLbls addNewDLbls() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTDLbls target = null;
            target = (CTDLbls)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDLbls() {
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
    public List<CTTrendline> getTrendlineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTrendline>(this::getTrendlineArray, this::setTrendlineArray, this::insertNewTrendline, this::removeTrendline, this::sizeOfTrendlineArray);
        }
    }

    @Override
    public CTTrendline[] getTrendlineArray() {
        return (CTTrendline[])this.getXmlObjectArray(PROPERTY_QNAME[8], (XmlObject[])new CTTrendline[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrendline getTrendlineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrendline target = null;
            target = (CTTrendline)this.get_store().find_element_user(PROPERTY_QNAME[8], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTrendlineArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setTrendlineArray(CTTrendline[] trendlineArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])trendlineArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setTrendlineArray(int i, CTTrendline trendline) {
        this.generatedSetterHelperImpl((XmlObject)trendline, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrendline insertNewTrendline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrendline target = null;
            target = (CTTrendline)this.get_store().insert_element_user(PROPERTY_QNAME[8], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTrendline addNewTrendline() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTrendline target = null;
            target = (CTTrendline)this.get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTrendline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTErrBars getErrBars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTErrBars target = null;
            target = (CTErrBars)((Object)this.get_store().find_element_user(PROPERTY_QNAME[9], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetErrBars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    @Override
    public void setErrBars(CTErrBars errBars) {
        this.generatedSetterHelperImpl(errBars, PROPERTY_QNAME[9], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTErrBars addNewErrBars() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTErrBars target = null;
            target = (CTErrBars)((Object)this.get_store().add_element_user(PROPERTY_QNAME[9]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetErrBars() {
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
    public CTAxDataSource getCat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxDataSource target = null;
            target = (CTAxDataSource)((Object)this.get_store().find_element_user(PROPERTY_QNAME[10], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    @Override
    public void setCat(CTAxDataSource cat) {
        this.generatedSetterHelperImpl(cat, PROPERTY_QNAME[10], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAxDataSource addNewCat() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAxDataSource target = null;
            target = (CTAxDataSource)((Object)this.get_store().add_element_user(PROPERTY_QNAME[10]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCat() {
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
    public CTNumDataSource getVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumDataSource target = null;
            target = (CTNumDataSource)((Object)this.get_store().find_element_user(PROPERTY_QNAME[11], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    @Override
    public void setVal(CTNumDataSource val) {
        this.generatedSetterHelperImpl(val, PROPERTY_QNAME[11], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTNumDataSource addNewVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTNumDataSource target = null;
            target = (CTNumDataSource)((Object)this.get_store().add_element_user(PROPERTY_QNAME[11]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetVal() {
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
    public CTShape getShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShape target = null;
            target = (CTShape)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    @Override
    public void setShape(CTShape shape) {
        this.generatedSetterHelperImpl(shape, PROPERTY_QNAME[12], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShape addNewShape() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShape target = null;
            target = (CTShape)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetShape() {
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
    public CTExtensionList getExtLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtensionList target = null;
            target = (CTExtensionList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[13], 0));
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
            return this.get_store().count_elements(PROPERTY_QNAME[13]) != 0;
        }
    }

    @Override
    public void setExtLst(CTExtensionList extLst) {
        this.generatedSetterHelperImpl(extLst, PROPERTY_QNAME[13], 0, (short)1);
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
            target = (CTExtensionList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[13]));
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
            this.get_store().remove_element(PROPERTY_QNAME[13], 0);
        }
    }
}

