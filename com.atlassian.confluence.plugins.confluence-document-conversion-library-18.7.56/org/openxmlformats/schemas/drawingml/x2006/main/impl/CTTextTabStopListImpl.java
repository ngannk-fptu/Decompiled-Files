/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;

public class CTTextTabStopListImpl
extends XmlComplexContentImpl
implements CTTextTabStopList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tab")};

    public CTTextTabStopListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTextTabStop> getTabList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextTabStop>(this::getTabArray, this::setTabArray, this::insertNewTab, this::removeTab, this::sizeOfTabArray);
        }
    }

    @Override
    public CTTextTabStop[] getTabArray() {
        return (CTTextTabStop[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTTextTabStop[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextTabStop getTabArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextTabStop target = null;
            target = (CTTextTabStop)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfTabArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setTabArray(CTTextTabStop[] tabArray) {
        this.check_orphaned();
        this.arraySetterHelper(tabArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setTabArray(int i, CTTextTabStop tab) {
        this.generatedSetterHelperImpl(tab, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextTabStop insertNewTab(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextTabStop target = null;
            target = (CTTextTabStop)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextTabStop addNewTab() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextTabStop target = null;
            target = (CTTextTabStop)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTab(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

