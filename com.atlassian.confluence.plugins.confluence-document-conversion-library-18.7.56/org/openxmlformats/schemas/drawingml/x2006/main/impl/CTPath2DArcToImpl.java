/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DArcTo;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;

public class CTPath2DArcToImpl
extends XmlComplexContentImpl
implements CTPath2DArcTo {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "wR"), new QName("", "hR"), new QName("", "stAng"), new QName("", "swAng")};

    public CTPath2DArcToImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getWR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STAdjCoordinate xgetWR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjCoordinate target = null;
            target = (STAdjCoordinate)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setWR(Object wr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setObjectValue(wr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetWR(STAdjCoordinate wr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjCoordinate target = null;
            target = (STAdjCoordinate)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (STAdjCoordinate)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(wr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getHR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STAdjCoordinate xgetHR() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjCoordinate target = null;
            target = (STAdjCoordinate)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHR(Object hr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setObjectValue(hr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHR(STAdjCoordinate hr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjCoordinate target = null;
            target = (STAdjCoordinate)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (STAdjCoordinate)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.set(hr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getStAng() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STAdjAngle xgetStAng() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjAngle target = null;
            target = (STAdjAngle)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setStAng(Object stAng) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setObjectValue(stAng);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetStAng(STAdjAngle stAng) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjAngle target = null;
            target = (STAdjAngle)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (STAdjAngle)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(stAng);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getSwAng() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target == null ? null : target.getObjectValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STAdjAngle xgetSwAng() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjAngle target = null;
            target = (STAdjAngle)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSwAng(Object swAng) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.setObjectValue(swAng);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSwAng(STAdjAngle swAng) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAdjAngle target = null;
            target = (STAdjAngle)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (STAdjAngle)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.set(swAng);
        }
    }
}

