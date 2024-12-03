/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAnchorClientData;

public class CTAnchorClientDataImpl
extends XmlComplexContentImpl
implements CTAnchorClientData {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "fLocksWithSheet"), new QName("", "fPrintsWithSheet")};

    public CTAnchorClientDataImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getFLocksWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[0]));
            }
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetFLocksWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(PROPERTY_QNAME[0]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFLocksWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFLocksWithSheet(boolean fLocksWithSheet) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setBooleanValue(fLocksWithSheet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFLocksWithSheet(XmlBoolean fLocksWithSheet) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(fLocksWithSheet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFLocksWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean getFPrintsWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[1]));
            }
            return target == null ? false : target.getBooleanValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBoolean xgetFPrintsWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (XmlBoolean)this.get_default_attribute_value(PROPERTY_QNAME[1]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFPrintsWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFPrintsWithSheet(boolean fPrintsWithSheet) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setBooleanValue(fPrintsWithSheet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFPrintsWithSheet(XmlBoolean fPrintsWithSheet) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBoolean target = null;
            target = (XmlBoolean)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (XmlBoolean)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.set(fPrintsWithSheet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFPrintsWithSheet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[1]);
        }
    }
}

