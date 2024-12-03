/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.word.STHorizontalAnchor
 *  com.microsoft.schemas.office.word.STVerticalAnchor
 *  com.microsoft.schemas.office.word.STWrapSide
 */
package com.microsoft.schemas.office.word.impl;

import com.microsoft.schemas.office.word.CTWrap;
import com.microsoft.schemas.office.word.STHorizontalAnchor;
import com.microsoft.schemas.office.word.STVerticalAnchor;
import com.microsoft.schemas.office.word.STWrapSide;
import com.microsoft.schemas.office.word.STWrapType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTWrapImpl
extends XmlComplexContentImpl
implements CTWrap {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "type"), new QName("", "side"), new QName("", "anchorx"), new QName("", "anchory")};

    public CTWrapImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STWrapType.Enum getType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : (STWrapType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STWrapType xgetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STWrapType target = null;
            target = (STWrapType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetType() {
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
    public void setType(STWrapType.Enum type) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setEnumValue(type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetType(STWrapType type) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STWrapType target = null;
            target = (STWrapType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (STWrapType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetType() {
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
    public STWrapSide.Enum getSide() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target == null ? null : (STWrapSide.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STWrapSide xgetSide() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STWrapSide target = null;
            target = (STWrapSide)this.get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSide() {
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
    public void setSide(STWrapSide.Enum side) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setEnumValue(side);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSide(STWrapSide side) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STWrapSide target = null;
            target = (STWrapSide)this.get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (STWrapSide)this.get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set((XmlObject)side);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSide() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[1]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STHorizontalAnchor.Enum getAnchorx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : (STHorizontalAnchor.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STHorizontalAnchor xgetAnchorx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STHorizontalAnchor target = null;
            target = (STHorizontalAnchor)this.get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAnchorx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[2]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAnchorx(STHorizontalAnchor.Enum anchorx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setEnumValue(anchorx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAnchorx(STHorizontalAnchor anchorx) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STHorizontalAnchor target = null;
            target = (STHorizontalAnchor)this.get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (STHorizontalAnchor)this.get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set((XmlObject)anchorx);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAnchorx() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[2]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STVerticalAnchor.Enum getAnchory() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target == null ? null : (STVerticalAnchor.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STVerticalAnchor xgetAnchory() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVerticalAnchor target = null;
            target = (STVerticalAnchor)this.get_store().find_attribute_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAnchory() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[3]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAnchory(STVerticalAnchor.Enum anchory) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.setEnumValue(anchory);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAnchory(STVerticalAnchor anchory) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STVerticalAnchor target = null;
            target = (STVerticalAnchor)this.get_store().find_attribute_user(PROPERTY_QNAME[3]);
            if (target == null) {
                target = (STVerticalAnchor)this.get_store().add_attribute_user(PROPERTY_QNAME[3]);
            }
            target.set((XmlObject)anchory);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAnchory() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[3]);
        }
    }
}

