/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComments;

public class CTCommentsImpl
extends XmlComplexContentImpl
implements CTComments {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "comment")};

    public CTCommentsImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTComment> getCommentList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTComment>(this::getCommentArray, this::setCommentArray, this::insertNewComment, this::removeComment, this::sizeOfCommentArray);
        }
    }

    @Override
    public CTComment[] getCommentArray() {
        return (CTComment[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTComment[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComment getCommentArray(int i) {
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
    public int sizeOfCommentArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setCommentArray(CTComment[] commentArray) {
        this.check_orphaned();
        this.arraySetterHelper(commentArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setCommentArray(int i, CTComment comment) {
        this.generatedSetterHelperImpl(comment, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTComment insertNewComment(int i) {
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
    public CTComment addNewComment() {
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
    public void removeComment(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

