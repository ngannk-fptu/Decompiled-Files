/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSiteList;

public class CTConnectionSiteListImpl
extends XmlComplexContentImpl
implements CTConnectionSiteList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cxn")};

    public CTConnectionSiteListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTConnectionSite> getCxnList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTConnectionSite>(this::getCxnArray, this::setCxnArray, this::insertNewCxn, this::removeCxn, this::sizeOfCxnArray);
        }
    }

    @Override
    public CTConnectionSite[] getCxnArray() {
        return (CTConnectionSite[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTConnectionSite[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnectionSite getCxnArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnectionSite target = null;
            target = (CTConnectionSite)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfCxnArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCxnArray(CTConnectionSite[] cxnArray) {
        this.check_orphaned();
        this.arraySetterHelper(cxnArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCxnArray(int i, CTConnectionSite cxn) {
        this.generatedSetterHelperImpl(cxn, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnectionSite insertNewCxn(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnectionSite target = null;
            target = (CTConnectionSite)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTConnectionSite addNewCxn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTConnectionSite target = null;
            target = (CTConnectionSite)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCxn(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

