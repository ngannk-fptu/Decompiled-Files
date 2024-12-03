/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

public class CTTextBodyImpl
extends XmlComplexContentImpl
implements CTTextBody {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bodyPr"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lstStyle"), new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "p")};

    public CTTextBodyImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextBodyProperties getBodyPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextBodyProperties target = null;
            target = (CTTextBodyProperties)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], 0));
            return target == null ? null : target;
        }
    }

    @Override
    public void setBodyPr(CTTextBodyProperties bodyPr) {
        this.generatedSetterHelperImpl(bodyPr, PROPERTY_QNAME[0], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextBodyProperties addNewBodyPr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextBodyProperties target = null;
            target = (CTTextBodyProperties)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextListStyle getLstStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextListStyle target = null;
            target = (CTTextListStyle)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], 0));
            return target == null ? null : target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetLstStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]) != 0;
        }
    }

    @Override
    public void setLstStyle(CTTextListStyle lstStyle) {
        this.generatedSetterHelperImpl(lstStyle, PROPERTY_QNAME[1], 0, (short)1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextListStyle addNewLstStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextListStyle target = null;
            target = (CTTextListStyle)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetLstStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTextParagraph> getPList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextParagraph>(this::getPArray, this::setPArray, this::insertNewP, this::removeP, this::sizeOfPArray);
        }
    }

    @Override
    public CTTextParagraph[] getPArray() {
        return (CTTextParagraph[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTTextParagraph[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraph getPArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraph target = null;
            target = (CTTextParagraph)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
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
    public int sizeOfPArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setPArray(CTTextParagraph[] pArray) {
        this.check_orphaned();
        this.arraySetterHelper(pArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setPArray(int i, CTTextParagraph p) {
        this.generatedSetterHelperImpl(p, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraph insertNewP(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraph target = null;
            target = (CTTextParagraph)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextParagraph addNewP() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextParagraph target = null;
            target = (CTTextParagraph)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeP(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }
}

