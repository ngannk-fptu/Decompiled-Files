/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;

public class CTDrawingImpl
extends XmlComplexContentImpl
implements CTDrawing {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "anchor"), new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "inline")};

    public CTDrawingImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAnchor> getAnchorList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAnchor>(this::getAnchorArray, this::setAnchorArray, this::insertNewAnchor, this::removeAnchor, this::sizeOfAnchorArray);
        }
    }

    @Override
    public CTAnchor[] getAnchorArray() {
        return (CTAnchor[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTAnchor[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnchor getAnchorArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnchor target = null;
            target = (CTAnchor)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfAnchorArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setAnchorArray(CTAnchor[] anchorArray) {
        this.check_orphaned();
        this.arraySetterHelper(anchorArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setAnchorArray(int i, CTAnchor anchor) {
        this.generatedSetterHelperImpl(anchor, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnchor insertNewAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnchor target = null;
            target = (CTAnchor)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnchor addNewAnchor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnchor target = null;
            target = (CTAnchor)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnchor(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTInline> getInlineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTInline>(this::getInlineArray, this::setInlineArray, this::insertNewInline, this::removeInline, this::sizeOfInlineArray);
        }
    }

    @Override
    public CTInline[] getInlineArray() {
        return (CTInline[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTInline[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInline getInlineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInline target = null;
            target = (CTInline)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
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
    public int sizeOfInlineArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setInlineArray(CTInline[] inlineArray) {
        this.check_orphaned();
        this.arraySetterHelper(inlineArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setInlineArray(int i, CTInline inline) {
        this.generatedSetterHelperImpl(inline, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInline insertNewInline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInline target = null;
            target = (CTInline)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInline addNewInline() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInline target = null;
            target = (CTInline)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }
}

