/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathArg;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSub;
import org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubPr;

public class CTSSubImpl
extends XmlComplexContentImpl
implements CTSSub {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sSubPr"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "e"), new QName("http://schemas.openxmlformats.org/officeDocument/2006/math", "sub")};

    public CTSSubImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSubPr getSSubPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSubPr target = null;
            target = (CTSSubPr)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSSubPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setSSubPr(CTSSubPr sSubPr) {
        this.generatedSetterHelperImpl(sSubPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSSubPr addNewSSubPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSSubPr target = null;
            target = (CTSSubPr)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSSubPr() {
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
    public CTOMathArg getE() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setE(CTOMathArg e) {
        this.generatedSetterHelperImpl(e, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathArg addNewE() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathArg getSub() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setSub(CTOMathArg sub) {
        this.generatedSetterHelperImpl(sub, PROPERTY_QNAME[2], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTOMathArg addNewSub() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTOMathArg target = null;
            target = (CTOMathArg)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }
}

