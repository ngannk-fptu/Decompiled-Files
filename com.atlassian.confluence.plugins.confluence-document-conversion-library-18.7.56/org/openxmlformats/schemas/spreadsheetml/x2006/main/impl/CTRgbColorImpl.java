/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedIntHex;

public class CTRgbColorImpl
extends XmlComplexContentImpl
implements CTRgbColor {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("", "rgb")};

    public CTRgbColorImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getRgb() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STUnsignedIntHex xgetRgb() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STUnsignedIntHex target = null;
            target = (STUnsignedIntHex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRgb() {
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
    public void setRgb(byte[] rgb) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setByteArrayValue(rgb);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRgb(STUnsignedIntHex rgb) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STUnsignedIntHex target = null;
            target = (STUnsignedIntHex)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (STUnsignedIntHex)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(rgb);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRgb() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[0]);
        }
    }
}

