/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;

public class CTCommentListImpl
extends XmlComplexContentImpl
implements CTCommentList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cm")};

    public CTCommentListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTComment> getCmList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTComment>(this::getCmArray, this::setCmArray, this::insertNewCm, this::removeCm, this::sizeOfCmArray);
        }
    }

    @Override
    public CTComment[] getCmArray() {
        return (CTComment[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTComment[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComment getCmArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTComment target = null;
            target = (CTComment)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCmArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCmArray(CTComment[] cmArray) {
        this.check_orphaned();
        this.arraySetterHelper(cmArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCmArray(int i, CTComment cm) {
        this.generatedSetterHelperImpl(cm, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComment insertNewCm(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTComment target = null;
            target = (CTComment)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComment addNewCm() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTComment target = null;
            target = (CTComment)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCm(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

