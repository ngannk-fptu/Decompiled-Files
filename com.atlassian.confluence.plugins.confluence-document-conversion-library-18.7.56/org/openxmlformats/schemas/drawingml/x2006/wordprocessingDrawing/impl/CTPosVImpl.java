/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignV
 */
package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosV;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignV;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STPositionOffset;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromV;

public class CTPosVImpl
extends XmlComplexContentImpl
implements CTPosV {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "align"), new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "posOffset"), new QName("", "relativeFrom")};

    public CTPosVImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STAlignV.Enum getAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : (STAlignV.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STAlignV xgetAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAlignV target = null;
            target = (STAlignV)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAlign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAlign(STAlignV.Enum align) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            }
            target.setEnumValue(align);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAlign(STAlignV align) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STAlignV target = null;
            target = (STAlignV)this.get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (STAlignV)this.get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set((XmlObject)align);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAlign() {
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
    public int getPosOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? 0 : target.getIntValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPositionOffset xgetPosOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositionOffset target = null;
            target = (STPositionOffset)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPosOffset() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPosOffset(int posOffset) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.setIntValue(posOffset);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPosOffset(STPositionOffset posOffset) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPositionOffset target = null;
            target = (STPositionOffset)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            if (target == null) {
                target = (STPositionOffset)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            }
            target.set(posOffset);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPosOffset() {
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
    public STRelFromV.Enum getRelativeFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : (STRelFromV.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STRelFromV xgetRelativeFrom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STRelFromV target = null;
            target = (STRelFromV)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRelativeFrom(STRelFromV.Enum relativeFrom) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setEnumValue(relativeFrom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRelativeFrom(STRelFromV relativeFrom) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STRelFromV target = null;
            target = (STRelFromV)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (STRelFromV)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(relativeFrom);
        }
    }
}

