/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderZOrder
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBottomPageBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTopPageBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderDisplay;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderOffset;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageBorderZOrder;

public class CTPageBordersImpl
extends XmlComplexContentImpl
implements CTPageBorders {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "top"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "left"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bottom"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "right"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "zOrder"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "display"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "offsetFrom")};

    public CTPageBordersImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTopPageBorder getTop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTopPageBorder target = null;
            target = (CTTopPageBorder)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setTop(CTTopPageBorder top) {
        this.generatedSetterHelperImpl(top, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTopPageBorder addNewTop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTopPageBorder target = null;
            target = (CTTopPageBorder)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTop() {
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
    public CTPageBorder getLeft() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBorder target = null;
            target = (CTPageBorder)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLeft() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setLeft(CTPageBorder left) {
        this.generatedSetterHelperImpl(left, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBorder addNewLeft() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBorder target = null;
            target = (CTPageBorder)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLeft() {
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
    public CTBottomPageBorder getBottom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBottomPageBorder target = null;
            target = (CTBottomPageBorder)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBottom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    @Override
    public void setBottom(CTBottomPageBorder bottom) {
        this.generatedSetterHelperImpl(bottom, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBottomPageBorder addNewBottom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBottomPageBorder target = null;
            target = (CTBottomPageBorder)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBottom() {
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
    public CTPageBorder getRight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBorder target = null;
            target = (CTPageBorder)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    @Override
    public void setRight(CTPageBorder right) {
        this.generatedSetterHelperImpl(right, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPageBorder addNewRight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPageBorder target = null;
            target = (CTPageBorder)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRight() {
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
    public STPageBorderZOrder.Enum getZOrder() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[4]));
            }
            return target == null ? null : (STPageBorderZOrder.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPageBorderZOrder xgetZOrder() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPageBorderZOrder target = null;
            target = (STPageBorderZOrder)this.get_store().find_attribute_user(PROPERTY_QNAME[4]);
            if (target == null) {
                target = (STPageBorderZOrder)this.get_default_attribute_value(PROPERTY_QNAME[4]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetZOrder() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[4]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setZOrder(STPageBorderZOrder.Enum zOrder) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.setEnumValue(zOrder);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetZOrder(STPageBorderZOrder zOrder) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPageBorderZOrder target = null;
            target = (STPageBorderZOrder)this.get_store().find_attribute_user(PROPERTY_QNAME[4]);
            if (target == null) {
                target = (STPageBorderZOrder)this.get_store().add_attribute_user(PROPERTY_QNAME[4]);
            }
            target.set((XmlObject)zOrder);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetZOrder() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[4]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPageBorderDisplay.Enum getDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            return target == null ? null : (STPageBorderDisplay.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPageBorderDisplay xgetDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPageBorderDisplay target = null;
            target = (STPageBorderDisplay)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[5]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDisplay(STPageBorderDisplay.Enum display) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
            }
            target.setEnumValue(display);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDisplay(STPageBorderDisplay display) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPageBorderDisplay target = null;
            target = (STPageBorderDisplay)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[5]));
            if (target == null) {
                target = (STPageBorderDisplay)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[5]));
            }
            target.set(display);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDisplay() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[5]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPageBorderOffset.Enum getOffsetFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[6]));
            }
            return target == null ? null : (STPageBorderOffset.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPageBorderOffset xgetOffsetFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPageBorderOffset target = null;
            target = (STPageBorderOffset)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (STPageBorderOffset)this.get_default_attribute_value(PROPERTY_QNAME[6]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOffsetFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[6]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOffsetFrom(STPageBorderOffset.Enum offsetFrom) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
            }
            target.setEnumValue(offsetFrom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetOffsetFrom(STPageBorderOffset offsetFrom) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPageBorderOffset target = null;
            target = (STPageBorderOffset)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[6]));
            if (target == null) {
                target = (STPageBorderOffset)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[6]));
            }
            target.set(offsetFrom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOffsetFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[6]);
        }
    }
}

