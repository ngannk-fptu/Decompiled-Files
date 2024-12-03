/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;

public class CTCommentAuthorListImpl
extends XmlComplexContentImpl
implements CTCommentAuthorList {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cmAuthor")};

    public CTCommentAuthorListImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCommentAuthor> getCmAuthorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCommentAuthor>(this::getCmAuthorArray, this::setCmAuthorArray, this::insertNewCmAuthor, this::removeCmAuthor, this::sizeOfCmAuthorArray);
        }
    }

    @Override
    public CTCommentAuthor[] getCmAuthorArray() {
        return (CTCommentAuthor[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTCommentAuthor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCommentAuthor getCmAuthorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCommentAuthor target = null;
            target = (CTCommentAuthor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfCmAuthorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCmAuthorArray(CTCommentAuthor[] cmAuthorArray) {
        this.check_orphaned();
        this.arraySetterHelper(cmAuthorArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCmAuthorArray(int i, CTCommentAuthor cmAuthor) {
        this.generatedSetterHelperImpl(cmAuthor, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCommentAuthor insertNewCmAuthor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCommentAuthor target = null;
            target = (CTCommentAuthor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCommentAuthor addNewCmAuthor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCommentAuthor target = null;
            target = (CTCommentAuthor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCmAuthor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

