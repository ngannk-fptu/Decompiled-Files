/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlinks;

public class CTHyperlinksImpl
extends XmlComplexContentImpl
implements CTHyperlinks {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "hyperlink")};

    public CTHyperlinksImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTHyperlink> getHyperlinkList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHyperlink>(this::getHyperlinkArray, this::setHyperlinkArray, this::insertNewHyperlink, this::removeHyperlink, this::sizeOfHyperlinkArray);
        }
    }

    @Override
    public CTHyperlink[] getHyperlinkArray() {
        return (CTHyperlink[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTHyperlink[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHyperlink getHyperlinkArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHyperlink target = null;
            target = (CTHyperlink)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfHyperlinkArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setHyperlinkArray(CTHyperlink[] hyperlinkArray) {
        this.check_orphaned();
        this.arraySetterHelper(hyperlinkArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setHyperlinkArray(int i, CTHyperlink hyperlink) {
        this.generatedSetterHelperImpl(hyperlink, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHyperlink insertNewHyperlink(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHyperlink target = null;
            target = (CTHyperlink)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHyperlink addNewHyperlink() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHyperlink target = null;
            target = (CTHyperlink)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHyperlink(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

