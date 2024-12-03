/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHandoutMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHandoutMasterIdListEntry;

public class CTHandoutMasterIdListImpl
extends XmlComplexContentImpl
implements CTHandoutMasterIdList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "handoutMasterId")};

    public CTHandoutMasterIdListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHandoutMasterIdListEntry getHandoutMasterId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHandoutMasterIdListEntry target = null;
            target = (CTHandoutMasterIdListEntry)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHandoutMasterId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]) != 0;
        }
    }

    @Override
    public void setHandoutMasterId(CTHandoutMasterIdListEntry handoutMasterId) {
        this.generatedSetterHelperImpl(handoutMasterId, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHandoutMasterIdListEntry addNewHandoutMasterId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHandoutMasterIdListEntry target = null;
            target = (CTHandoutMasterIdListEntry)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHandoutMasterId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], 0);
        }
    }
}

