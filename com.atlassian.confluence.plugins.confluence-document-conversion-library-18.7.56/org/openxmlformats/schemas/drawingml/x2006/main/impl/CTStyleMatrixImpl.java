/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackgroundFillStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;

public class CTStyleMatrixImpl
extends XmlComplexContentImpl
implements CTStyleMatrix {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillStyleLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnStyleLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectStyleLst"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bgFillStyleLst"), new QName("", "name")};

    public CTStyleMatrixImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillStyleList getFillStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillStyleList target = null;
            target = (CTFillStyleList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setFillStyleLst(CTFillStyleList fillStyleLst) {
        this.generatedSetterHelperImpl(fillStyleLst, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFillStyleList addNewFillStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFillStyleList target = null;
            target = (CTFillStyleList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineStyleList getLnStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineStyleList target = null;
            target = (CTLineStyleList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setLnStyleLst(CTLineStyleList lnStyleLst) {
        this.generatedSetterHelperImpl(lnStyleLst, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLineStyleList addNewLnStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLineStyleList target = null;
            target = (CTLineStyleList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectStyleList getEffectStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectStyleList target = null;
            target = (CTEffectStyleList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setEffectStyleLst(CTEffectStyleList effectStyleLst) {
        this.generatedSetterHelperImpl(effectStyleLst, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEffectStyleList addNewEffectStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEffectStyleList target = null;
            target = (CTEffectStyleList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBackgroundFillStyleList getBgFillStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBackgroundFillStyleList target = null;
            target = (CTBackgroundFillStyleList)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setBgFillStyleLst(CTBackgroundFillStyleList bgFillStyleLst) {
        this.generatedSetterHelperImpl(bgFillStyleLst, PROPERTY_QNAME[3], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBackgroundFillStyleList addNewBgFillStyleLst() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBackgroundFillStyleList target = null;
            target = (CTBackgroundFillStyleList)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[4]));
            }
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (XmlString)this.get_default_attribute_value(PROPERTY_QNAME[4]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetName() {
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
    public void setName(String name) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.setStringValue(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetName(XmlString name) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[4]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[4]));
            }
            target.set(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetName() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[4]);
        }
    }
}

