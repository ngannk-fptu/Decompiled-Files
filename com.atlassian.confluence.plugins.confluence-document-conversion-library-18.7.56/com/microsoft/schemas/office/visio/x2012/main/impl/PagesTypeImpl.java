/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.PageType;
import com.microsoft.schemas.office.visio.x2012.main.PagesType;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PagesTypeImpl
extends XmlComplexContentImpl
implements PagesType {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.microsoft.com/office/visio/2012/main", "Page")};

    public PagesTypeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<PageType> getPageList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<PageType>(this::getPageArray, this::setPageArray, this::insertNewPage, this::removePage, this::sizeOfPageArray);
        }
    }

    @Override
    public PageType[] getPageArray() {
        return (PageType[])this.getXmlObjectArray(PROPERTY_QNAME[0], new PageType[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PageType getPageArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PageType target = null;
            target = (PageType)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfPageArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setPageArray(PageType[] pageArray) {
        this.check_orphaned();
        this.arraySetterHelper(pageArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setPageArray(int i, PageType page) {
        this.generatedSetterHelperImpl(page, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PageType insertNewPage(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PageType target = null;
            target = (PageType)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PageType addNewPage() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            PageType target = null;
            target = (PageType)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePage(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

