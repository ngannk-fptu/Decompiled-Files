/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.math.BigInteger;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPermStart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STEdGrp;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTPermImpl;

public class CTPermStartImpl
extends CTPermImpl
implements CTPermStart {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "edGrp"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ed"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "colFirst"), new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "colLast")};

    public CTPermStartImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STEdGrp.Enum getEdGrp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target == null ? null : (STEdGrp.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STEdGrp xgetEdGrp() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STEdGrp target = null;
            target = (STEdGrp)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEdGrp() {
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
    public void setEdGrp(STEdGrp.Enum edGrp) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.setEnumValue(edGrp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetEdGrp(STEdGrp edGrp) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STEdGrp target = null;
            target = (STEdGrp)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[0]));
            if (target == null) {
                target = (STEdGrp)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[0]));
            }
            target.set(edGrp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEdGrp() {
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
    public String getEd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STString xgetEd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STString target = null;
            target = (STString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEd() {
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
    public void setEd(String ed) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.setStringValue(ed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetEd(STString ed) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STString target = null;
            target = (STString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[1]));
            if (target == null) {
                target = (STString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[1]));
            }
            target.set(ed);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEd() {
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
    public BigInteger getColFirst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target == null ? null : target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STDecimalNumber xgetColFirst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDecimalNumber target = null;
            target = (STDecimalNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetColFirst() {
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
    public void setColFirst(BigInteger colFirst) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.setBigIntegerValue(colFirst);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColFirst(STDecimalNumber colFirst) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDecimalNumber target = null;
            target = (STDecimalNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[2]));
            if (target == null) {
                target = (STDecimalNumber)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[2]));
            }
            target.set(colFirst);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetColFirst() {
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
    public BigInteger getColLast() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target == null ? null : target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STDecimalNumber xgetColLast() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDecimalNumber target = null;
            target = (STDecimalNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetColLast() {
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
    public void setColLast(BigInteger colLast) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.setBigIntegerValue(colLast);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetColLast(STDecimalNumber colLast) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDecimalNumber target = null;
            target = (STDecimalNumber)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[3]));
            if (target == null) {
                target = (STDecimalNumber)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[3]));
            }
            target.set(colLast);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetColLast() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[3]);
        }
    }
}

