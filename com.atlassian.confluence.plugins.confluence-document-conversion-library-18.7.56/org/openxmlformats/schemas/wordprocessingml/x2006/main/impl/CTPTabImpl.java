/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabAlignment
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabLeader
 *  org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabRelativeTo
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabLeader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabRelativeTo;

public class CTPTabImpl
extends XmlComplexContentImpl
implements CTPTab {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alignment"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "relativeTo"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "leader")};

    public CTPTabImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPTabAlignment.Enum getAlignment() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : (STPTabAlignment.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPTabAlignment xgetAlignment() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPTabAlignment target = null;
            target = (STPTabAlignment)this.get_store().find_attribute_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAlignment(STPTabAlignment.Enum alignment) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setEnumValue(alignment);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAlignment(STPTabAlignment alignment) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPTabAlignment target = null;
            target = (STPTabAlignment)this.get_store().find_attribute_user(PROPERTY_QNAME[0]);
            if (target == null) {
                target = (STPTabAlignment)this.get_store().add_attribute_user(PROPERTY_QNAME[0]);
            }
            target.set((XmlObject)alignment);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPTabRelativeTo.Enum getRelativeTo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target == null ? null : (STPTabRelativeTo.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPTabRelativeTo xgetRelativeTo() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPTabRelativeTo target = null;
            target = (STPTabRelativeTo)this.get_store().find_attribute_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRelativeTo(STPTabRelativeTo.Enum relativeTo) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setEnumValue(relativeTo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRelativeTo(STPTabRelativeTo relativeTo) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPTabRelativeTo target = null;
            target = (STPTabRelativeTo)this.get_store().find_attribute_user(PROPERTY_QNAME[1]);
            if (target == null) {
                target = (STPTabRelativeTo)this.get_store().add_attribute_user(PROPERTY_QNAME[1]);
            }
            target.set((XmlObject)relativeTo);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPTabLeader.Enum getLeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : (STPTabLeader.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STPTabLeader xgetLeader() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPTabLeader target = null;
            target = (STPTabLeader)this.get_store().find_attribute_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setLeader(STPTabLeader.Enum leader) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setEnumValue(leader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetLeader(STPTabLeader leader) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STPTabLeader target = null;
            target = (STPTabLeader)this.get_store().find_attribute_user(PROPERTY_QNAME[2]);
            if (target == null) {
                target = (STPTabLeader)this.get_store().add_attribute_user(PROPERTY_QNAME[2]);
            }
            target.set((XmlObject)leader);
        }
    }
}

