/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjustHandleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSiteList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DList;

public class CTCustomGeometry2DImpl
extends XmlComplexContentImpl
implements CTCustomGeometry2D {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "avLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gdLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ahLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cxnLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "rect"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pathLst")};

    public CTCustomGeometry2DImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomGuideList getAvLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuideList target = null;
            target = (CTGeomGuideList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAvLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setAvLst(CTGeomGuideList avLst) {
        this.generatedSetterHelperImpl(avLst, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomGuideList addNewAvLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuideList target = null;
            target = (CTGeomGuideList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAvLst() {
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
    public CTGeomGuideList getGdLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuideList target = null;
            target = (CTGeomGuideList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGdLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setGdLst(CTGeomGuideList gdLst) {
        this.generatedSetterHelperImpl(gdLst, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomGuideList addNewGdLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomGuideList target = null;
            target = (CTGeomGuideList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGdLst() {
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
    public CTAdjustHandleList getAhLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAdjustHandleList target = null;
            target = (CTAdjustHandleList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAhLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setAhLst(CTAdjustHandleList ahLst) {
        this.generatedSetterHelperImpl(ahLst, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAdjustHandleList addNewAhLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAdjustHandleList target = null;
            target = (CTAdjustHandleList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAhLst() {
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
    public CTConnectionSiteList getCxnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnectionSiteList target = null;
            target = (CTConnectionSiteList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCxnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setCxnLst(CTConnectionSiteList cxnLst) {
        this.generatedSetterHelperImpl(cxnLst, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnectionSiteList addNewCxnLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnectionSiteList target = null;
            target = (CTConnectionSiteList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCxnLst() {
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
    public CTGeomRect getRect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomRect target = null;
            target = (CTGeomRect)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    @Override
    public void setRect(CTGeomRect rect) {
        this.generatedSetterHelperImpl(rect, PROPERTY_QNAME[4], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTGeomRect addNewRect() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTGeomRect target = null;
            target = (CTGeomRect)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRect() {
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
    public CTPath2DList getPathLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPath2DList target = null;
            target = (CTPath2DList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setPathLst(CTPath2DList pathLst) {
        this.generatedSetterHelperImpl(pathLst, PROPERTY_QNAME[5], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPath2DList addNewPathLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPath2DList target = null;
            target = (CTPath2DList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }
}

