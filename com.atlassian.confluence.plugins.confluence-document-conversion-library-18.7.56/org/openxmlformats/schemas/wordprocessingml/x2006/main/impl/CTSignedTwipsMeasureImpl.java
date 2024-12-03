/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;

public class CTSignedTwipsMeasureImpl
extends XmlComplexContentImpl
implements CTSignedTwipsMeasure {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val")};

    public CTSignedTwipsMeasureImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getVal() {
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
    public STSignedTwipsMeasure xgetVal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignedTwipsMeasure target = null;
            target = (STSignedTwipsMeasure)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setVal(Object val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setObjectValue(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetVal(STSignedTwipsMeasure val) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STSignedTwipsMeasure target = null;
            target = (STSignedTwipsMeasure)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (STSignedTwipsMeasure)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(val);
        }
    }
}

