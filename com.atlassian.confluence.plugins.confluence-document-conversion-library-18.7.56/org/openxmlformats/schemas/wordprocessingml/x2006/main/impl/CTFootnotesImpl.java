/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;

public class CTFootnotesImpl
extends XmlComplexContentImpl
implements CTFootnotes {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnote")};

    public CTFootnotesImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFtnEdn> getFootnoteList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFtnEdn>(this::getFootnoteArray, this::setFootnoteArray, this::insertNewFootnote, this::removeFootnote, this::sizeOfFootnoteArray);
        }
    }

    @Override
    public CTFtnEdn[] getFootnoteArray() {
        return (CTFtnEdn[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTFtnEdn[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdn getFootnoteArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdn target = null;
            target = (CTFtnEdn)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
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
    public int sizeOfFootnoteArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setFootnoteArray(CTFtnEdn[] footnoteArray) {
        this.check_orphaned();
        this.arraySetterHelper(footnoteArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setFootnoteArray(int i, CTFtnEdn footnote) {
        this.generatedSetterHelperImpl(footnote, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdn insertNewFootnote(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdn target = null;
            target = (CTFtnEdn)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFtnEdn addNewFootnote() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFtnEdn target = null;
            target = (CTFtnEdn)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFootnote(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }
}

